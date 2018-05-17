package com.huanke.iot.job.stat;

import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.dao.impl.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.po.device.DevicePo;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年05月16日 20:27
 **/

@Repository
public class DeviceSensorStatWorker {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;
    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;

    private DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(16,
            new DefaultThreadFactory("StatSensorThread"));
    @Scheduled(cron = "0 10/30 * * * ?")
    public void doWork(){
        DevicePo queryPo = new DevicePo();
        int offset = 0;
        List<DevicePo> devicePoList = deviceMapper.selectList(queryPo,100,offset);
        do{
            Long startTime = new DateTime().withMillisOfSecond(0).plusMinutes(-40).getMillis();
            Long endTime = new DateTime().withMillisOfSecond(0).plusMinutes(-40).getMillis();
            devicePoList.forEach(devicePo -> {

            });
        }while (devicePoList.size() > 0);
    }
}
