package com.huanke.iot.gateway.onlinecheck;

import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OnlineCheckService {

    private ConcurrentHashMap<Integer, OnlineCheckData> idMap =
            new ConcurrentHashMap<>(2048);

    @Autowired
    private DeviceMapper deviceMapper;

    @Scheduled(cron = "0/10 * * * * * ?")
    public void doScan() {

        for (Map.Entry<Integer, OnlineCheckData> entry : idMap.entrySet()) {
            OnlineCheckData data = entry.getValue();
            if (data.getLastUpdateTime() < (System.currentTimeMillis() - 1000)) {
                data.setFailCount(data.getFailCount() + 1);
                if (data.getFailCount() > 3) {
                    data.setOnline(false);
                }
            }
        }

        for (Map.Entry<Integer, OnlineCheckData> entry : idMap.entrySet()) {
            OnlineCheckData data = entry.getValue();
            if (!data.isOnline()) {
                DevicePo updatePo = new DevicePo();
                updatePo.setId(data.getId());
                updatePo.setOnlineStatus(2);
                deviceMapper.updateById(updatePo);
            }
        }


    }


    public void addDataToScan(OnlineCheckData onlineCheckData) {
        idMap.putIfAbsent(onlineCheckData.getId(), onlineCheckData);
    }
}
