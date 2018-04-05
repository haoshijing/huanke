package com.huanke.iot.manage.controller;

import com.huanke.iot.manage.request.DeviceQueryRequest;
import com.huanke.iot.manage.response.DeviceVo;
import com.huanke.iot.base.api.ApiResponse;
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

    @RequestMapping("/queryList")
    public ApiResponse<List<DeviceVo>> queryList(@RequestBody DeviceQueryRequest deviceQueryRequest){
        return null;
    }

    @RequestMapping("/queryCount")
    public ApiResponse<Integer> queryCount(@RequestBody  DeviceQueryRequest deviceQueryRequest){
        return new ApiResponse<>(0);
    }

    @RequestMapping("/insertDeviceType")
    ApiResponse<Boolean> insertDeviceType(){
        return null;
    }
}
