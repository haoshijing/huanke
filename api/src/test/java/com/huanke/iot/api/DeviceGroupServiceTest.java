package com.huanke.iot.api;

import com.huanke.iot.api.service.device.group.DeviceGroupService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
public class DeviceGroupServiceTest extends BaseTest {

    @Autowired
    DeviceGroupService deviceGroupService;

    @Test
    public void testInsertGroup(){

    }
}
