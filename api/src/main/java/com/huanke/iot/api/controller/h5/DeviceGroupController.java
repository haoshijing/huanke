package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.group.DeviceGroupNewRequest;
import com.huanke.iot.api.controller.h5.group.DeviceGroupRequest;
import com.huanke.iot.api.service.device.group.DeviceGroupService;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author haoshijing
 * @version 2018年04月10日 10:16
 **/
@RestController
@RequestMapping("/h5/api")
public class DeviceGroupController extends BaseController{

    @Autowired
    DeviceGroupService deviceGroupService;
    @RequestMapping("/createGroup")
    public ApiResponse<Integer> createDeviceGroup(HttpServletRequest request, @RequestBody DeviceGroupNewRequest deviceGroupNewRequest){
        Integer userId = getCurrentUserId(request);
        Integer groupId = deviceGroupService.createDeviceGroup(userId,deviceGroupNewRequest);
        return new ApiResponse<>(groupId);
    }

    @RequestMapping("/deleteGroup")
    public ApiResponse<Boolean> delDeviceGroup(HttpServletRequest request,Integer groupId){
        Integer userId = getCurrentUserId(request);
        Boolean ret  = deviceGroupService.deleteGroup(userId,groupId);
        return new ApiResponse<>(ret);
    }
    @RequestMapping("/updateDeviceGroup")
    public ApiResponse<Boolean> updateDeviceGroup(HttpServletRequest request,@RequestBody DeviceGroupRequest deviceGroupRequest){
        Integer userId = getCurrentUserId(request);
        Boolean ret = deviceGroupService.updateDeviceGroup(userId,deviceGroupRequest);
        return new ApiResponse<>(ret);
    }
}
