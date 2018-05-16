package com.huanke.iot.job.stat;

import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

/**
 * @author haoshijing
 * @version 2018年05月16日 20:27
 **/

@Repository
public class DeviceSensorStatWorker {

    @Autowired
    private DeviceMapper deviceMapper;
    @Scheduled(cron = "0 10/30 * * * ?")
    public void doWork(){
        DevicePo queryPo = new DevicePo();
        int offset = 0;
        deviceMapper.selectList(queryPo,100,offset);
    }
}
