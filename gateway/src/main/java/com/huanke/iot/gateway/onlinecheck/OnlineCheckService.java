package com.huanke.iot.gateway.onlinecheck;

import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
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
public class OnlineCheckService {

    private ConcurrentHashMap<Integer, OnlineCheckData> idMap =
            new ConcurrentHashMap<>(2048);
    @Autowired
    private DeviceMapper deviceMapper;

    private long l = System.currentTimeMillis();

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init(){
        loadOnlineFromDb();
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
        },0,15, TimeUnit.SECONDS);
    }

    public void doScan() {

        Iterator<Map.Entry<Integer, OnlineCheckData>> it = idMap.entrySet().iterator();
        long temp = System.currentTimeMillis();
        while (it.hasNext()){
            OnlineCheckData data = it.next().getValue();
            if (data.getLastUpdateTime() < l ) {
                data.setFailCount(data.getFailCount() + 1);
                if (data.getFailCount() > 3) {
                    data.setOnline(false);
                }
            }
            if(!data.isOnline()){
                DevicePo updatePo = new DevicePo();
                updatePo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_NO);
                updatePo.setId(data.getId());
                deviceMapper.updateById(updatePo);
                //将离线记录写入日志
                DeviceOperLogPo deviceOperLogPo= new DeviceOperLogPo();
                deviceOperLogPo.setDeviceId(data.getId());
                deviceOperLogPo.setFuncId("410");
                deviceOperLogPo.setFuncValue("0");
                deviceOperLogPo.setCreateTime(System.currentTimeMillis());
                this.deviceOperLogMapper.insert(deviceOperLogPo);
                it.remove();
            }
        }
        l = temp;
    }

    public void resetOnline(Integer deviceId){
        OnlineCheckData data = idMap.get(deviceId);
        boolean needUpdateDd  = false;
        if(data == null){
            data = new OnlineCheckData();
            needUpdateDd = true;
        }else if(!data.isOnline()){
            needUpdateDd = true;
        }
        data.setFailCount(0);
        data.setLastUpdateTime(System.currentTimeMillis());
        data.setOnline(true);
        log.debug("当前设备状态:",data.isOnline());
        //上次记录为离线时才更新状态
        data.setId(deviceId);
        idMap.put(deviceId,data);
       if(needUpdateDd){
           DevicePo updatePo = new DevicePo();
           updatePo.setId(deviceId);
           updatePo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_YES);
           updatePo.setLastOnlineTime(System.currentTimeMillis());
           deviceMapper.updateById(updatePo);
           //记录离线后的首次上线时间
           DeviceOperLogPo deviceOperLogPo= new DeviceOperLogPo();
           deviceOperLogPo.setDeviceId(deviceId);
           deviceOperLogPo.setFuncId("410");
           deviceOperLogPo.setFuncValue("1");
           deviceOperLogPo.setCreateTime(System.currentTimeMillis());
           this.deviceOperLogMapper.insert(deviceOperLogPo);
       }
    }

    private void loadOnlineFromDb(){
        DevicePo queryPo = new DevicePo();
        queryPo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_YES);
        List<DevicePo> devicePoList = deviceMapper.selectList(queryPo,100000,0);
        devicePoList.forEach(devicePo -> {
            OnlineCheckData data = new OnlineCheckData();
            data.setFailCount(0);
            data.setLastUpdateTime(System.currentTimeMillis());
            data.setOnline(true);
            data.setId(devicePo.getId());
            idMap.put(devicePo.getId(),data);
        });
    }
}
