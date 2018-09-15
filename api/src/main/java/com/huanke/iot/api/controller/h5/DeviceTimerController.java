package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.DeviceTimerRequest;
import com.huanke.iot.api.controller.h5.req.QueryTimerRequest;
import com.huanke.iot.api.controller.h5.req.TimerStatusRequest;
import com.huanke.iot.api.controller.h5.response.DeviceTimerVo;
import com.huanke.iot.api.controller.h5.response.DictVo;
import com.huanke.iot.api.service.device.timer.DeviceTimerService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.constant.TimerConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/h5/api")
@Slf4j
public class DeviceTimerController extends BaseController{
    @RequestMapping("/timerType")
    public List<DictVo> timerType(){
        List<DictVo> dictVoList = deviceTimerService.getTimeTypes();
        return dictVoList;
    }

    @Autowired
    private DeviceTimerService deviceTimerService;
    @RequestMapping("/addTimer")
    public ApiResponse<Integer> addTimer(@RequestBody DeviceTimerRequest request){
        List<Integer> daysOfWeek = request.getDaysOfWeek();
        Integer type = request.getType();
        Integer hour = request.getHour();
        Integer minute = request.getMinute();
        Integer second = request.getSecond();
        Long afterTime = request.getAfterTime();
        if(type == TimerConstants.TIMER_TYPE_IDEA){
            if(afterTime == null || daysOfWeek.isEmpty() || hour == null || minute == null || second == null){
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数错误", 0);
            }
        }
        Integer userId = getCurrentUserId();
        request.setUserId(userId);
        Integer timeId = deviceTimerService.insertTimer(request);
        return new ApiResponse<>(timeId);
    }

    @RequestMapping("/queryTimerList")
    public Object queryTimerList(@RequestBody QueryTimerRequest request){
        Integer userId = getCurrentUserId();
        String wxDeviceId = request.getWxDeviceId();
        Integer type = request.getType();
        if(wxDeviceId.equals("") || wxDeviceId == null){
            return new ApiResponse<>(RetCode.PARAM_ERROR, "参数错误");
        }
        log.info("查询定时列表：userId={}, wxDeviceId={}, type={}", userId, wxDeviceId, type);
        List<DeviceTimerVo> deviceTimerVos = deviceTimerService.queryTimerList(userId,wxDeviceId,type);
        return new ApiResponse<>(deviceTimerVos);
    }

    @RequestMapping("/editTimer")
    public ApiResponse<Boolean> editDeviceTimer(HttpServletRequest request,@RequestBody DeviceTimerRequest deviceTimerRequest){
        Integer userId = getCurrentUserId();
        deviceTimerRequest.setUserId(userId);
        Boolean ret = deviceTimerService.editTimer(userId,deviceTimerRequest);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/timerDetail/{timerId}")
    public ApiResponse<DeviceTimerVo> detail(@PathVariable("timerId") Integer timerId){
        return new ApiResponse<>(deviceTimerService.getById(timerId));
    }

    @RequestMapping("/cancelTimer")
    public ApiResponse<Boolean> cancelTimer(@RequestBody TimerStatusRequest request){
        Integer userId = getCurrentUserId();
        Integer timerId = request.getTimerId();
        Integer status = request.getStatus();
        if(timerId == null || status == null){
            return new ApiResponse<>(RetCode.PARAM_ERROR, "");
        }
        Boolean ret =  deviceTimerService.updateTimerStatus(userId,timerId,status);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/deleteTimer/{timerId}")
    public ApiResponse<Boolean> deleteTimer(@PathVariable("timerId") Integer timerId){
        Integer userId = getCurrentUserId();
        Boolean ret =  deviceTimerService.updateTimerStatus(userId,timerId,3);
        return new ApiResponse<>(ret);
    }

}
