package com.huanke.iot.manage.controller.device;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.manage.controller.device.request.DeviceGroupQueryRequest;
import com.huanke.iot.manage.controller.device.request.DeviceGroupUpdateVo;
import com.huanke.iot.manage.controller.device.request.type.DeviceTypeCreateUpdateVo;
import com.huanke.iot.manage.controller.device.request.type.DeviceTypeQueryRequest;
import com.huanke.iot.manage.controller.device.request.type.DeviceTypeResponseVo;
import com.huanke.iot.manage.controller.device.response.DeviceGroupItemVo;
import com.huanke.iot.manage.service.DeviceGroupService;
import com.huanke.iot.manage.service.device.DeviceTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年06月01日 12:56
 **/
@RestController
@RequestMapping("/api/device")
public class DeviceTypeController {
    @Autowired
    private DeviceTypeService deviceTypeService;
    @RequestMapping("/typeList")
    public ApiResponse<List<DeviceTypeResponseVo>> selectList(@RequestBody DeviceTypeQueryRequest request){
        List<DeviceTypeResponseVo> typeResponseVos = deviceTypeService.selectList(request);
        return new ApiResponse<>(typeResponseVos);
    }

    @RequestMapping("/update")
    public ApiResponse<Boolean> updateDeviceType(@RequestBody DeviceTypeCreateUpdateVo deviceTypeCreateUpdateVo){
        if(!checkRequestValid(deviceTypeCreateUpdateVo)){
            return ApiResponse.PARAM_ERROR;
        }
        Boolean ret = deviceTypeService.createOrUpdate(deviceTypeCreateUpdateVo);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/create")
    public ApiResponse<Boolean> createDeviceType(@RequestBody DeviceTypeCreateUpdateVo deviceTypeCreateUpdateVo){
        if(!checkRequestValid(deviceTypeCreateUpdateVo)){
            return ApiResponse.PARAM_ERROR;
        }
        Boolean ret = deviceTypeService.createOrUpdate(deviceTypeCreateUpdateVo);
        return new ApiResponse<>(ret);
    }
    @RequestMapping("/selectCount")
    public ApiResponse<Integer> selectCount(@RequestBody DeviceTypeQueryRequest request){
        Integer count = deviceTypeService.selectCount(request);
        return new ApiResponse<>(count);
    }

    private boolean checkRequestValid(DeviceTypeCreateUpdateVo deviceTypeCreateUpdateVo){
        String funcList = deviceTypeCreateUpdateVo.getFuncList();
        String sensorList = deviceTypeCreateUpdateVo.getSensorList();
        if(StringUtils.isEmpty(funcList) || StringUtils.isEmpty(sensorList)){
            return true;
        }
        return false;
    }
}
