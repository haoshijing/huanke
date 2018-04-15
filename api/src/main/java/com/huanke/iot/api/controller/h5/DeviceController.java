package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.group.DeviceGroupNewRequest;
import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.api.service.device.basic.DeviceService;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:33
 **/
@RequestMapping("/h5/api")
@RestController
public class DeviceController extends BaseController {

    @Autowired
    private DeviceService deviceService;
    @RequestMapping("/obtainMyDevice")
    public ApiResponse<DeviceListVo> obtainMyDevice(HttpServletRequest httpServletRequest){
        Integer userId = getCurrentUserId(httpServletRequest);
        DeviceListVo deviceListVo = deviceService.obtainMyDevice(userId);
        return new ApiResponse<>(deviceListVo);
    }

    @RequestMapping("/queryDetailByDeviceId/{deviceId}")
    public ApiResponse<DeviceDetailVo> queryDetailByDeviceId(@PathVariable String deviceId){
        return new ApiResponse<>(new DeviceDetailVo());
    }

    @RequestMapping("/editDevice")
    public ApiResponse<Boolean> editDevice(HttpServletRequest request,String deviceId,String deviceName){
        Integer userId = getCurrentUserId(request);
        boolean ret =  deviceService.editDevice(userId,deviceId,deviceName);
        return new ApiResponse<>(ret);
    }

}
