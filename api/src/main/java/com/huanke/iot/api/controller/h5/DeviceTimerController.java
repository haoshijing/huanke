package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.DeviceTimerRequest;
import com.huanke.iot.api.controller.h5.response.DeviceTimerVo;
import com.huanke.iot.api.service.device.timer.DeviceTimerService;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/h5/api")
public class DeviceTimerController extends BaseController{

    @Autowired
    private DeviceTimerService deviceTimerService;
    @RequestMapping("/addTimer")
    public ApiResponse<Boolean> addTimer(@RequestBody DeviceTimerRequest deviceTimerRequest){
        Integer userId = getCurrentUserId();
        deviceTimerRequest.setUserId(userId);
        Boolean ret = deviceTimerService.insertTimer(deviceTimerRequest);
        return new ApiResponse<>(ret);
    }


    @RequestMapping("/queryTimerList")
    public ApiResponse<List<DeviceTimerVo>> queryTimerList(String deviceIdStr,Integer timerType){

        Integer userId = getCurrentUserId();

        List<DeviceTimerVo> deviceTimerVos = deviceTimerService.queryTimerList(userId,deviceIdStr,timerType);
        return new ApiResponse<>(deviceTimerVos);
    }

    @RequestMapping("/editTimer")
    public ApiResponse<Boolean> editDeviceTimer(HttpServletRequest request,@RequestBody DeviceTimerRequest deviceTimerRequest){
        Integer userId = getCurrentUserId();
        deviceTimerRequest.setUserId(userId);
        Boolean ret = deviceTimerService.editTimer(userId,deviceTimerRequest);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/timerDetail")
    public ApiResponse<DeviceTimerVo> detail(Integer timerId){
        return new ApiResponse<>(deviceTimerService.getById(timerId));
    }

    @RequestMapping("/cancelTimer")
    public ApiResponse<Boolean> cancelTimer(Integer timerId,Integer status){
        Integer userId = getCurrentUserId();
        Boolean ret =  deviceTimerService.updateTimerStatus(userId,timerId,status);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/deleteTimer")
    public ApiResponse<Boolean> deleteTimer(Integer timerId){
        Integer userId = getCurrentUserId();
        Boolean ret =  deviceTimerService.updateTimerStatus(userId,timerId,3);
        return new ApiResponse<>(ret);
    }

}
