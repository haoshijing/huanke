package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.DeviceGroupFuncVo;
import com.huanke.iot.api.controller.h5.response.DeviceGroupVo;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.api.service.device.team.DeviceGroupService;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月24日 10:16
 **/
@RestController
@RequestMapping("/h5/api/group")
@Slf4j
public class DeviceGroupController extends BaseController{
    @Autowired
    private DeviceGroupService deviceGroupService;
    @Autowired
    private DeviceDataService deviceDataService;


    @RequestMapping("/list")
    public Object groupList() {
        Integer userId = getCurrentUserId();
        Integer customerId = getCurrentCustomerId();
        log.info("查询设备群列表： userId={}, customerId={}", userId, customerId);
        List<DeviceGroupVo> deviceGroupVoList = deviceGroupService.getGroupListByUserId(userId, customerId);
        return new ApiResponse<>(deviceGroupVoList);
    }

    @RequestMapping("/groupSendFunc")
    public Object groupSendFunc(@RequestBody DeviceGroupFuncVo deviceGroupFuncVo) {
        deviceDataService.sendGroupFunc(deviceGroupFuncVo, getCurrentUserId(), 1);
        return new ApiResponse<>();
    }
}
