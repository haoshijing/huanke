package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.DeviceTimerRequest;
import com.huanke.iot.api.controller.h5.response.DeviceTimerVo;
import com.huanke.iot.api.service.device.timer.DeviceTimerService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import org.assertj.core.util.Lists;
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
    public ApiResponse<Boolean> addTimer(HttpServletRequest request,@RequestBody DeviceTimerRequest deviceTimerRequest){
        Integer userId = getCurrentUserId(request);
        deviceTimerRequest.setUserId(userId);
        Boolean ret = deviceTimerService.insertTimer(deviceTimerRequest);
        return new ApiResponse<>(ret);
    }


    @RequestMapping("/queryTimerList")
    public ApiResponse<List<DeviceTimerVo>> queryTimerList(HttpServletRequest request,String deviceIdStr,Integer timerType){

        Integer userId = getCurrentUserId(request);

        List<DeviceTimerVo> deviceTimerVos = deviceTimerService.queryTimerList(userId,deviceIdStr,timerType);
        return new ApiResponse<>(deviceTimerVos);
    }

    @RequestMapping("/cancelTimer")
    public ApiResponse<Boolean> cancelTimer(HttpServletRequest httpServletRequest,Integer timerId){
        Integer userId = getCurrentUserId(httpServletRequest);
        Boolean ret =  deviceTimerService.cancelTimer(userId,timerId);
        return new ApiResponse<>(ret);
    }
}
