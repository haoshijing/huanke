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

/**
 * @author onlymark
 * @version 2018年08月22日
 **/
@RestController
@RequestMapping("/h5/api")
public class DeviceTeamController extends BaseController{

    @Autowired
    DeviceGroupService deviceGroupService;
    @RequestMapping("/createGroup")
    public ApiResponse<Integer> createDeviceGroup( @RequestBody DeviceGroupNewRequest deviceGroupNewRequest){
        Integer userId = getCurrentUserId();
        Integer groupId = deviceGroupService.createDeviceGroup(userId,deviceGroupNewRequest);
        return new ApiResponse<>(groupId);
    }

    @RequestMapping("/deleteGroup")
    public ApiResponse<Boolean> delDeviceGroup(Integer groupId){
        Integer userId = getCurrentUserId();
        Boolean ret  = deviceGroupService.deleteGroup(userId,groupId);
        return new ApiResponse<>(ret);
    }
    @RequestMapping("/updateDeviceGroup")
    public ApiResponse<Boolean> updateDeviceGroup(@RequestBody DeviceGroupRequest deviceGroupRequest){
        Integer userId = getCurrentUserId();
        Boolean ret = deviceGroupService.updateDeviceGroup(userId,deviceGroupRequest);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/updateGroupName")
    public ApiResponse<Boolean> updateGroupName(Integer groupId,String groupName){
        Integer userId = getCurrentUserId();
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
