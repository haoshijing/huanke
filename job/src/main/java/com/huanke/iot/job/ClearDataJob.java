package com.huanke.iot.job;

import com.huanke.iot.base.dao.device.data.DeviceControlMapper;
import com.huanke.iot.base.dao.device.data.DeviceSensorDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 描述:
 * 清除设备多余数据任务
 *
 * @author onlymark
 * @create 2018-11-15 上午9:41
 */
@Repository
@Slf4j
public class ClearDataJob {
    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;
    @Autowired
    private DeviceControlMapper deviceControlMapper;

    @Scheduled(cron = "0 5 2 * * ?")
    public void clearControlData(){
        long lastFiveTime = new Date().getTime() - (5 * 60 * 1000);
        deviceControlMapper.clearData(lastFiveTime);
    }

    @Scheduled(cron = "0 5 3 * * ?")
    public void clearSensorData(){
        long lastFiveTime = new Date().getTime() - (5 * 60 * 1000);
        deviceSensorDataMapper.clearData(lastFiveTime);
    }
}
