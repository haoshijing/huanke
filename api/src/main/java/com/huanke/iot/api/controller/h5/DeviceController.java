package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.service.device.group.DeviceGroupService;
import com.huanke.iot.api.controller.h5.group.DeviceNewGroupRequest;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:33
 **/
@RequestMapping("/h5/api")
//@RestController
public class DeviceController {

    @Autowired
    private DeviceGroupService deviceGroupService;
    @RequestMapping("/addNewDeviceGroup")
    public ApiResponse<Integer> addNewDeviceGroup(@RequestBody DeviceNewGroupRequest deviceNewGroupRequest){
        if(StringUtils.isEmpty(deviceNewGroupRequest.getGroupName()) || CollectionUtils.isEmpty(deviceNewGroupRequest.getDeviceIds())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"");
        }
        Integer groupId = deviceGroupService.addNewDeviceGroup(deviceNewGroupRequest);
        return new ApiResponse<>(groupId);
    }

}
