package com.huanke.iot.gateway.powercheck;

import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class PowerCheckService {

    private ConcurrentHashMap<Integer, PowerCheckData> idMap =
            new ConcurrentHashMap<>(2048);

    @Autowired
    private DeviceMapper deviceMapper;


    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init(){
        loadPowerStatusFromDb();
        executorService = Executors.newScheduledThreadPool(1,new DefaultThreadFactory("ScanIdThread"));
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    doScan();
                }catch (Exception e){
                    log.error("",e);
                }
            }
        },1,15, TimeUnit.SECONDS);
    }

    public void doScan() {

        Iterator<Map.Entry<Integer, PowerCheckData>> it = idMap.entrySet().iterator();
        while (it.hasNext()){
            PowerCheckData data = it.next().getValue();
            if (data.getLastUpdateTime() < (System.currentTimeMillis() - 15000)) {
                data.setFailCount(data.getFailCount() + 1);
                if (data.getFailCount() > 3) {
                    data.setPowerOn(false);
                }
            }
            if(!data.isPowerOn()){
                DevicePo updatePo = new DevicePo();
                updatePo.setPowerStatus(DeviceConstant.POWER_STATUS__NO);
                updatePo.setId(data.getId());
                deviceMapper.updateById(updatePo);
                it.remove();
            }
        }
    }

    public void resetPowerStatus(Integer id){
        PowerCheckData data =   idMap.get(id);
        boolean needUpdateDd  =false;
        if(data == null){
            data = new PowerCheckData();
        }
        data.setFailCount(0);
        data.setLastUpdateTime(System.currentTimeMillis());
        data.setPowerOn(true);
        DevicePo devicePo = this.deviceMapper.selectById(id);
        log.info("当前设备开关机:",devicePo.getPowerStatus());
        //上次记录为关机时才更新状态
        if(DeviceConstant.POWER_STATUS__NO == devicePo.getPowerStatus()) {
            needUpdateDd = true;
        }
        data.setId(id);
        idMap.put(id,data);
        if(needUpdateDd){
            DevicePo updatePo = new DevicePo();
            updatePo.setId(id);
            updatePo.setPowerStatus(DeviceConstant.POWER_STATUS_YES);
            deviceMapper.updateById(updatePo);
        }
    }

    private void loadPowerStatusFromDb(){
        DevicePo queryPo = new DevicePo();
        queryPo.setPowerStatus(DeviceConstant.POWER_STATUS_YES);
        List<DevicePo> devicePoList = deviceMapper.selectList(queryPo,100000,0);
        devicePoList.forEach(devicePo -> {
            resetPowerStatus(devicePo.getId());
        });
    }
}
