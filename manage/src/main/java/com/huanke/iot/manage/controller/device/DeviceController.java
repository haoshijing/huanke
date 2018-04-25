package com.huanke.iot.manage.controller.device;

import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.controller.device.request.DeviceQueryRequest;
import com.huanke.iot.manage.controller.device.request.DeviceUpdateRequest;
import com.huanke.iot.manage.response.DeviceTypeVo;
import com.huanke.iot.manage.response.DeviceVo;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.device.DeviceService;
import com.huanke.iot.manage.service.device.DeviceTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:11
 **/
@RestController
@RequestMapping("/api/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceTypeService deviceTypeService;
    @RequestMapping("/queryList")
    public ApiResponse<List<DeviceVo>> queryList(@RequestBody DeviceQueryRequest deviceQueryRequest){
        List<DeviceVo> deviceVos = deviceService.selectList(deviceQueryRequest);
        return new ApiResponse<>(deviceVos);
    }

    @RequestMapping("/queryCount")
    public ApiResponse<Integer> queryCount(@RequestBody  DeviceQueryRequest deviceQueryRequest){
        return new ApiResponse<>(deviceService.selectCount(deviceQueryRequest));
    }

    @RequestMapping("/updateDevice")
    public ApiResponse<Boolean> updateDevice(@RequestBody DeviceUpdateRequest deviceUpdateRequest){

        if(StringUtils.isEmpty(deviceUpdateRequest.getDeviceName())){
            return new ApiResponse(RetCode.PARAM_ERROR);
        }
        Boolean updateRet = deviceService.updateDevice(deviceUpdateRequest);
        return new ApiResponse<>(updateRet);
    }


    @RequestMapping("/queryTypeList")
    public ApiResponse<List<DeviceTypeVo>> queryTypeList(@RequestBody DeviceQueryRequest deviceQueryRequest){
        List<DeviceTypeVo> deviceVos = deviceTypeService.selectList(deviceQueryRequest);
        return new ApiResponse<>(deviceVos);
    }

    @RequestMapping("/queryTypeCount")
    public ApiResponse<Integer> queryTypeCount(@RequestBody DeviceQueryRequest deviceQueryRequest){
        Integer typeCount = deviceTypeService.selectCount(deviceQueryRequest);
        return new ApiResponse<>(typeCount);
    }

}
