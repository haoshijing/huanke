package com.huanke.iot.gateway.test;

import com.huanke.iot.base.dao.device.data.DeviceAlarmMapper;
import com.huanke.iot.base.po.device.data.DeviceAlarmPo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
public class DeviceMapperTest extends BaseTest {

    @Autowired
    private DeviceAlarmMapper deviceAlarmMapper;


    @Test
    public void testInsert(){
        DeviceAlarmPo deviceAlarmPo = new DeviceAlarmPo();
        deviceAlarmPo.setDeviceId(1);
        deviceAlarmPo.setIndex(0);
        deviceAlarmPo.setValue(2);
        deviceAlarmPo.setType(1);
        deviceAlarmPo.setCreateTime(System.currentTimeMillis());
        Assert.assertTrue( deviceAlarmMapper.insert(deviceAlarmPo) > 0);

    }
}
