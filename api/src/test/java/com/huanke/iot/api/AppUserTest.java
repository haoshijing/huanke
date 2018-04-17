package com.huanke.iot.api;

import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.user.AppUserPo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * @author haoshijing
 * @version 2018年04月09日 10:31
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplicationStarter.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class AppUserTest {
    @Autowired
    AppUserMapper appUserMapper;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Test
    public void testInsertOperLog(){
//        DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
//        deviceOperLogPo.setFuncId(1);
//        deviceOperLogPo.setFuncValue("1");
//        deviceOperLogPo.setDeviceId(1);
//        deviceOperLogPo.setRequestId(UUID.randomUUID().toString().replace("-",""));
//        deviceOperLogPo.setCreateTime(System.currentTimeMillis());
//        Assert.assertTrue( deviceOperLogMapper.insert(deviceOperLogPo) > 0);

        DeviceOperLogPo updateDeviceLog = new DeviceOperLogPo();
        updateDeviceLog.setRequestId("9b9b3200e35d434282dc2865d3b5c0cb");
        updateDeviceLog.setDealRet(1);
        updateDeviceLog.setResponseTime(System.currentTimeMillis());

        Assert.assertTrue(deviceOperLogMapper.updateByRequestId(updateDeviceLog) > 0);
    }

    @Test
    public void testInsertAppUser(){
        AppUserPo appUserPo = new AppUserPo();
        appUserPo.setOpenId("jshdjshdjshd");
        appUserPo.setCreateTime(System.currentTimeMillis());

        Assert.assertTrue(appUserMapper.insert(appUserPo) > 0);
    }

    @Test
    public void testQueryAndUpdate(){
        AppUserPo appUserPo = appUserMapper.selectByOpenId("jshdjshdjshd");
        appUserPo.setLastVisitTime(System.currentTimeMillis());
        Assert.assertTrue( appUserMapper.updateById(appUserPo) > 0);
    }

}
