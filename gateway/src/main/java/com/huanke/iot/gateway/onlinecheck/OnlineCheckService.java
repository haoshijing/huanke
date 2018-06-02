package com.huanke.iot.gateway.onlinecheck;

import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Iterator;
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

    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init(){
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

        Iterator<Map.Entry<Integer, OnlineCheckData>> it = idMap.entrySet().iterator();
        while (it.hasNext()){
            OnlineCheckData data = it.next().getValue();
            if (data.getLastUpdateTime() < (System.currentTimeMillis() - 1000)) {
                data.setFailCount(data.getFailCount() + 1);
                if (data.getFailCount() > 3) {
                    data.setOnline(false);
                }
            }
            if(!data.isOnline()){
                DevicePo updatePo = new DevicePo();
                updatePo.setOnlineStatus(2);
                updatePo.setId(it.next().getKey());
                deviceMapper.updateById(updatePo);
            }
        }
    }

    public void resetOnline(Integer id){
       OnlineCheckData data =   idMap.get(id);
       boolean needUpdateDd  =false;
       if(data == null){
           data = new OnlineCheckData();
           data.setFailCount(0);
           data.setLastUpdateTime(System.currentTimeMillis());
           data.setOnline(true);
           needUpdateDd = true;
           data.setId(id);
       }else{
           needUpdateDd = true;
           data.setFailCount(0);
           data.setLastUpdateTime(System.currentTimeMillis());
           data.setOnline(true);
       }
        idMap.put(id,data);
       if(needUpdateDd){
           DevicePo updatePo = new DevicePo();
           updatePo.setId(id);
           updatePo.setOnlineStatus(1);
           deviceMapper.updateById(updatePo);
       }
    }
}
