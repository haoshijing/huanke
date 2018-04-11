package com.huanke.iot.gateway.test;

import com.huanke.iot.base.dao.impl.device.data.DeviceAlarmMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorMapper;
import com.huanke.iot.base.po.device.data.DeviceAlarmPo;
import com.huanke.iot.base.po.device.data.DeviceSensorDataPo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
public class DeviceMapperTest extends BaseTest {

    @Autowired
    private DeviceAlarmMapper deviceAlarmMapper;

    @Autowired
    private DeviceSensorMapper deviceSensorMapper;

    @Test
    public void testInsert(){
        DeviceAlarmPo deviceAlarmPo = new DeviceAlarmPo();
        deviceAlarmPo.setDeviceId(1);
        deviceAlarmPo.setIndex(0);
        deviceAlarmPo.setValue(2);
        deviceAlarmPo.setType(1);
        deviceAlarmPo.setCreateTime(System.currentTimeMillis());
        Assert.assertTrue( deviceAlarmMapper.insert(deviceAlarmPo) > 0);

        DeviceSensorDataPo deviceSensorDataPo = new DeviceSensorDataPo();
        deviceSensorDataPo.setCo2(1);
        deviceSensorDataPo.setHcho(3);
        deviceSensorDataPo.setTemperature(40);
        deviceSensorDataPo.setPm2_5(4);
        deviceSensorDataPo.setIndex(0);
        deviceSensorDataPo.setHumidity(4);
        Assert.assertTrue(deviceSensorMapper.insert(deviceSensorDataPo) > 0);
    }
}
