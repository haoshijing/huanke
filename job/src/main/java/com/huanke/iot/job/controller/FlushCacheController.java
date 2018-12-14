package com.huanke.iot.job.controller;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/job/flushCache")
@Slf4j
public class FlushCacheController {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelAbilityOptionMapper;

    @Autowired
    private DeviceModelAbilityMapper deviceModelAbilityMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;
    @RequestMapping("/flushCache")
    public ApiResponse<Boolean> flushCache() {
        log.info("刷新缓存");
        customerMapper.flushCache();
        deviceAbilityMapper.flushCache();
        deviceAbilityOptionMapper.flushCache();
        deviceModelMapper.flushCache();
        deviceModelAbilityMapper.flushCache();
        deviceModelAbilityOptionMapper.flushCache();
        return new ApiResponse<>(true);
    }
}
