package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:33
 **/
@RequestMapping("/h5/api")
//@RestController
public class DeviceController {

    @RequestMapping("/obatinMyDevice")
    public ApiResponse<DeviceListVo> obatinMyDevice(){
        return new ApiResponse<>(new DeviceListVo());
    }

    @RequestMapping("/queryDetailByDeviceId/{deviceId}")
    public ApiResponse<DeviceDetailVo> queryDetailByDeviceId(@PathVariable String deviceId){
        return new ApiResponse<>(new DeviceDetailVo());
    }

    @RequestMapping("/editDevice")
    public ApiResponse<Boolean> editDevice(String deviceId,String deviceName){
        return new ApiResponse<>(true);
    }

}
