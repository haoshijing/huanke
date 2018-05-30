package com.huanke.iot.api.controller.h5;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.h5.req.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.response.*;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.api.service.device.basic.DeviceService;
import com.huanke.iot.api.vo.SpeedConfigRequest;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:33
 **/
@RequestMapping("/h5/api")
@RestController
public class DeviceController extends BaseController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @RequestMapping("/obtainMyDevice")
    public ApiResponse<DeviceListVo> obtainMyDevice(HttpServletRequest httpServletRequest) {
        Integer userId = getCurrentUserId(httpServletRequest);
        DeviceListVo deviceListVo = deviceService.obtainMyDevice(userId);
        return new ApiResponse<>(deviceListVo);
    }

    @RequestMapping("/queryDetailByDeviceId")
    public ApiResponse<DeviceDetailVo> queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = deviceDataService.queryDetailByDeviceId(deviceId);
        return new ApiResponse<>(deviceDetailVo);
    }

    @RequestMapping("/editDevice")
    public ApiResponse<Boolean> editDevice(HttpServletRequest request, String deviceId, String deviceName) {
        Integer userId = getCurrentUserId(request);
        boolean ret = deviceService.editDevice(userId, deviceId, deviceName);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/token")
    public ApiResponse<String> obtainShareToken(HttpServletRequest request, String deviceId) {
        String lastToken = stringRedisTemplate.opsForValue().get("token." + deviceId);
        if (StringUtils.isNotEmpty(lastToken)) {
            return new ApiResponse<>(lastToken);
        }
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        stringRedisTemplate.opsForValue().set("token." + deviceId, token);
        stringRedisTemplate.expire("token." + deviceId, 10, TimeUnit.MINUTES);
        return new ApiResponse<>(token);
    }

    @RequestMapping("/share")
    public ApiResponse<Boolean> shareDevice(HttpServletRequest request, String masterOpenId, String deviceId, String token) {
        Integer userId = getCurrentUserId(request);
        Boolean shareOk = deviceDataService.shareDevice(masterOpenId, userId, deviceId, token);
        return new ApiResponse<>(shareOk);
    }

    @RequestMapping("/clearRelation")
    public ApiResponse<Boolean> clearRelation(HttpServletRequest request, String deviceId, String joinOpenId) {
        Integer userId = getCurrentUserId(request);
        Boolean clearOk = deviceDataService.clearRelation(joinOpenId, userId, deviceId);
        return new ApiResponse<>(clearOk);
    }

    @RequestMapping("/updateDeviceLocation")
    public ApiResponse<Boolean> updateDeviceLocation(HttpServletRequest request,String deviceId,String location){
        if(StringUtils.isEmpty(location)){
            return new ApiResponse(RetCode.PARAM_ERROR);
        }
        Integer userId = getCurrentUserId(request);
        Boolean clearOk = deviceService.editDeviceLoc(userId, deviceId,location);
        return new ApiResponse<>(clearOk);
    }

    @RequestMapping("/shareList")
    public ApiResponse<List<DeviceShareVo>> shareList(HttpServletRequest request, String deviceId) {
        Integer userId = getCurrentUserId(request);
        List<DeviceShareVo> deviceShareVos = deviceDataService.shareList(userId, deviceId);
        return new ApiResponse<>(deviceShareVos);
    }

    @RequestMapping("/sendFunc")
    public ApiResponse<String> sendFunc(HttpServletRequest request,@RequestBody DeviceFuncVo deviceFuncVo) {
        String funcId = deviceFuncVo.getFuncId();
        if (StringUtils.isNotEmpty(funcId) && funcId.contains("33")) {
            deviceFuncVo.setValue(String.valueOf(3600*3000));
        }
        if(StringUtils.isNotEmpty(funcId) && funcId.contains("28")){
            if(StringUtils.equals(deviceFuncVo.getValue(),"0")){
                deviceFuncVo.setValue("1");
            }
        }
        String requestId = deviceDataService.sendFunc(deviceFuncVo,getCurrentUserId(request),1);
        return new ApiResponse<>(requestId);
    }

    @RequestMapping("/setSpeedConfig")
    public ApiResponse<Boolean> setSpeedConfig(@RequestBody SpeedConfigRequest request) {
        Boolean ret = deviceService.setSpeedConfig(request);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/queryResponse")
    public ApiResponse<JSONObject> queryResonse(String requestId) {
        DeviceOperLogPo deviceOperLogPo = deviceOperLogMapper.queryByRequestId(requestId);
        if (deviceOperLogPo != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ret", deviceOperLogPo.getDealRet());
            jsonObject.put("msg", deviceOperLogPo.getRetMsg());
            return new ApiResponse<>(jsonObject);
        }
        return new ApiResponse<>(new JSONObject());
    }

    @RequestMapping("/getSpeedConfig")
    public ApiResponse<DeviceSpeedConfigVo> getDeviceSpeedConfig(String deviceId) {
        DeviceSpeedConfigVo data = deviceService.getSpeed(deviceId);
        return new ApiResponse<>(data);
    }

    @RequestMapping("/getHistoryData")
    public ApiResponse<List<SensorDataVo>> getHistoryData(String deviceId, Integer type) {
        return new ApiResponse<>(deviceDataService.getHistoryData(deviceId, type));
    }
}
