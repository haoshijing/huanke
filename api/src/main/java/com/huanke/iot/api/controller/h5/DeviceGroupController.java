package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.group.DeviceGroupRequest;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoshijing
 * @version 2018年04月10日 10:16
 **/
@RestController
@RequestMapping("/h5/api")
public class DeviceGroupController {

    @RequestMapping("/createGroup")
    public ApiResponse<Integer> createDeviceGroup(String groupName){
        return new ApiResponse<>(1);
    }

    @RequestMapping("/deleteGroup")
    public ApiResponse<Boolean> delDeviceGroup(Integer groupId){
        return new ApiResponse<>(true);
    }
    @RequestMapping("/updateGroupDevice")
    public ApiResponse<Boolean> updateGroupDevice(@RequestBody DeviceGroupRequest deviceGroupRequest){
        return new ApiResponse<>(true);
    }
}
