package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.group.DeviceGroupNewRequest;
import com.huanke.iot.api.controller.h5.group.DeviceGroupRequest;
import com.huanke.iot.api.service.device.group.DeviceGroupService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import org.apache.commons.lang3.StringUtils;
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

    @RequestMapping("/updateGroupName")
    public ApiResponse<Boolean> updateGroupName(HttpServletRequest request,Integer groupId,String groupName){
        Integer userId = getCurrentUserId(request);
        if(groupId == null  || StringUtils.isEmpty(groupName)){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"参数错误");
        }
        ApiResponse apiResponse = new ApiResponse(true);
        Boolean ret = deviceGroupService.updateGroupName(userId,groupId,groupName);
        apiResponse.setData(ret);
        if(!ret){
            apiResponse.setMsg("该组名已存在");
        }
        return apiResponse;
    }
}
