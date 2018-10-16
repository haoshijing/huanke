package com.huanke.iot.api.controller.app;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.controller.app.response.AppInfoVo;
import com.huanke.iot.api.controller.h5.BaseController;
import com.huanke.iot.api.controller.h5.req.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.api.service.device.basic.DeviceService;
import com.huanke.iot.api.service.device.basic.AppBasicService;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RequestMapping("/app/api")
@Slf4j
@RestController
public class AppController extends BaseController {

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private AppBasicService appBasicService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private DeviceService deviceService;

    @Value("${apkKey}")
    private String apkKey;


    @RequestMapping("/setApkInfo")
    public boolean setApkInfo(HttpServletRequest request) {
        return appBasicService.addUserAppInfo(request);
    }


    @RequestMapping("/queryDeviceList")
    public ApiResponse<DeviceListVo> queryDeviceList() {
        Integer userId = getCurrentUserIdForApp();
        DeviceListVo deviceListVo = deviceService.obtainMyDevice(userId);
        return new ApiResponse<>(deviceListVo);
    }

    @RequestMapping("/queryDetailByDeviceId")
    public ApiResponse<DeviceDetailVo> queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = deviceDataService.queryDetailByDeviceId(deviceId);
        return new ApiResponse<>(deviceDetailVo);
    }

    /*@RequestMapping("/getHistoryData")
    public ApiResponse<List<SensorDataVo>> getHistoryData(Integer deviceId) {
        return new ApiResponse<>(deviceDataService.getHistoryData(deviceId), null);
    }*/

    @RequestMapping("/editDevice")
    public ApiResponse<Boolean> editDevice(Integer deviceId, String deviceName) {
        Integer userId = getCurrentUserIdForApp();
        boolean ret = deviceService.editDevice(userId, deviceId, deviceName);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/obtainApk")
    public ApiResponse<AppInfoVo> obtainApk() {

        String apkInfo = stringRedisTemplate.opsForValue().get(apkKey);
        if (StringUtils.isNotEmpty(apkInfo)) {
            AppInfoVo appInfoVo = JSON.parseObject(apkInfo, AppInfoVo.class);
            return new ApiResponse<>(appInfoVo);
        }
        return new ApiResponse<>();
    }

    @RequestMapping("/sendFunc")
    public ApiResponse<Boolean> sendFuc(Integer deviceId,String funcId){
        DeviceFuncVo deviceFuncVo = new DeviceFuncVo();
        /*if(StringUtils.equals("1", funcId)){
            deviceFuncVo.setWxDeviceId(deviceId);
            deviceFuncVo.setFuncId("210");
            deviceFuncVo.setValue("0");
        }else if(StringUtils.equals("2",funcId)){
            deviceFuncVo.setWxDeviceId(deviceId);
            deviceFuncVo.setFuncId("280");
            deviceFuncVo.setValue("1");
        }else if(StringUtils.equals("3",funcId)){
            deviceFuncVo.setWxDeviceId(deviceId);
            deviceFuncVo.setFuncId("280");
            deviceFuncVo.setValue("2");
        }else if(StringUtils.equals("4",funcId)){
            deviceFuncVo.setWxDeviceId(deviceId);
            deviceFuncVo.setFuncId("280");
            deviceFuncVo.setValue("3");
        }else if(StringUtils.equals("5",funcId) ||
                StringUtils.equals("6",funcId)){
            deviceFuncVo.setWxDeviceId(deviceId);
            deviceFuncVo.setFuncId("210");
            deviceFuncVo.setValue("1");
        }*/
        deviceDataService.sendFunc(deviceFuncVo,getCurrentUserIdForApp(),2);
        return new ApiResponse<>(true);
    }
}
