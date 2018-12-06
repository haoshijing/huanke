package com.huanke.iot.api.controller.app;

import com.huanke.iot.api.controller.app.response.AppDeviceDataVo;
import com.huanke.iot.api.controller.app.response.AppDeviceListVo;
import com.huanke.iot.api.controller.app.response.AppInfoVo;
import com.huanke.iot.api.controller.app.response.AppSceneVo;
import com.huanke.iot.api.controller.h5.BaseController;
import com.huanke.iot.api.controller.h5.req.*;
import com.huanke.iot.api.controller.h5.response.*;
import com.huanke.iot.api.service.device.basic.AppDeviceDataService;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.api.service.device.basic.DeviceService;
import com.huanke.iot.api.service.device.basic.AppBasicService;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RequestMapping("/app/api/base")
@Slf4j
@RestController
public class AppController extends BaseController {

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private AppBasicService appBasicService;

    @Autowired
    private AppDeviceDataService appDeviceDataService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private DeviceService deviceService;

    @Value("${apkKey}")
    private String apkKey;

    @RequestMapping("/removeIMeiInfo")
    public ApiResponse<Object> removeIMeiInfo(HttpServletRequest request) {
        return appBasicService.removeIMeiInfo(request);
    }

    @RequestMapping("/setApkInfo")
    public void setApkInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String objectApiResponse = appBasicService.addUserAppInfo(request);
        response.setContentType(ContentType.TEXT_HTML.getMimeType());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print("<span style='font-size:55px'>"+objectApiResponse+"</span>");
    }

    @RequestMapping("/getQRCode")
    public ApiResponse<Object> getQRCode(HttpServletRequest request) {
        String appId = request.getParameter("appId");
        return new ApiResponse<>(appBasicService.getQRCode(appId));
    }

    @RequestMapping("/queryDeviceList")
    public ApiResponse<AppDeviceListVo> queryDeviceList() {
        Integer userId = getCurrentUserIdForApp();
        log.debug("查询我的设备列表，userId={}", userId);
        AppDeviceListVo deviceListVo = appDeviceDataService.obtainMyDevice(userId);
        return new ApiResponse<>(deviceListVo);
    }

    @RequestMapping("/queryChildDevice/{hostDeviceId}")
    public Object obtainChildDevice(@PathVariable("hostDeviceId") Integer hostDeviceId) {
        Integer userId = getCurrentUserIdForApp();
        log.info("查询子设备列表，userId={}， hostDeviceId={}", userId, hostDeviceId);
        List<AppDeviceListVo.DeviceItemPo> childDeviceList = appDeviceDataService.queryChildDevice(hostDeviceId);
        return new ApiResponse<>(childDeviceList);
    }

    @RequestMapping("/getModelVo")
    public ApiResponse<DeviceModelVo> getModelVo(@RequestBody DeviceFormatRequest request) {
        Integer deviceId = request.getDeviceId();
        log.debug("获取功能项，deviceId={}", deviceId);
        DeviceModelVo deviceModelVo = appBasicService.getModelVo(deviceId);
        return new ApiResponse<>(deviceModelVo);
    }

    @RequestMapping("/queryDetailByDeviceId")
    public ApiResponse<List<AppDeviceDataVo>> queryDetailByDeviceId(@RequestBody DeviceAbilitysRequest request) {
        Integer deviceId = request.getDeviceId();
        log.debug("查询设备详情，设备ID={}",deviceId);
        List<Integer> abilityIds = request.getAbilityIds();
        List<AppDeviceDataVo> deviceAbilityVos = appDeviceDataService.queryDetailAbilitysValue(deviceId,abilityIds);
        return new ApiResponse<>(deviceAbilityVos);
    }

    @RequestMapping("/getWeatherAndLocation/{deviceId}")
    public ApiResponse<List<Object>> queryDeviceWeather(@PathVariable("deviceId") Integer deviceId) {
        log.debug("查询设备天气，设备ID={}",deviceId);
        WeatherVo weatherVo = deviceService.queryDeviceWeather(deviceId);
        LocationVo locationVo = deviceService.queryDeviceLocation(deviceId);
        if(locationVo.getArea().split(" ").length>3) {
            locationVo.setArea(locationVo.getArea().split(" ")[3]);
        }
        List resp = new ArrayList();
        resp.add(weatherVo);
        resp.add(locationVo);
        return new ApiResponse<>(resp);
    }

    @RequestMapping("/getHistoryData")
    public ApiResponse<List<SensorDataVo>> getHistoryData(@RequestBody HistoryDataRequest request) {
        Integer deviceId = request.getDeviceId();
        Integer type = request.getType();
        Integer userId = getCurrentUserIdForApp();
        log.debug("查询设备历史曲线：userId={}, deviceId={}, type={}", userId, deviceId, type);
        return new ApiResponse<>(appBasicService.getHistoryData(deviceId, type));
    }

    @RequestMapping("/editDevice")
    public ApiResponse<Boolean> editDevice(@RequestBody DeviceRequest request) {
        Integer deviceId = request.getDeviceId();
        String deviceName = request.getDeviceName();
        Integer userId = getCurrentUserIdForApp();
        boolean ret = deviceService.editDevice(userId, deviceId, deviceName);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/obtainApk")
    public ApiResponse<AppInfoVo> obtainApk(HttpServletRequest request) {
//        String apkInfo = stringRedisTemplate.opsForValue().get(apkKey);
//        if (StringUtils.isNotEmpty(apkInfo)) {
//            AppInfoVo appInfoVo = JSON.parseObject(apkInfo, AppInfoVo.class);
//            return new ApiResponse<>(appInfoVo);
//        }
//        return new ApiResponse<>();
        String appId = request.getParameter("appId");
        return new ApiResponse<>(appBasicService.getApkInfo(appId));
    }

    @RequestMapping("/sendFunc")
    public ApiResponse<Boolean> sendFuc(@RequestBody DeviceFuncVo deviceFuncVo){
        log.debug("发送指令："+deviceFuncVo.toString());
        String funcId = deviceFuncVo.getFuncId();
        Boolean request = deviceDataService.sendFuncs(deviceFuncVo,getCurrentUserIdForApp(),2);
        return new ApiResponse<>(request);
    }

    @RequestMapping("/getAppPassword")
    public ApiResponse<String> getAppPassword(HttpServletRequest request){
        String appId = request.getParameter("appId");
        log.debug("获取设备选择密码，appId={}",appId);
        String response= appBasicService.getPassword(appId);
        return new ApiResponse<>(response);
    }

    @RequestMapping("/getCustomerScene")
    public ApiResponse<AppSceneVo> getCustomerScene(){
        AppSceneVo request= appBasicService.getCustomerSceneInfo();
        return new ApiResponse<>(request);
    }
}
