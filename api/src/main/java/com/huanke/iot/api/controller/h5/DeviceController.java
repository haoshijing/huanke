package com.huanke.iot.api.controller.h5;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.h5.req.*;
import com.huanke.iot.api.controller.h5.response.*;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.api.service.device.basic.DeviceService;
import com.huanke.iot.api.service.device.team.DeviceTeamService;
import com.huanke.iot.api.vo.SpeedConfigRequest;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:33
 **/
@RequestMapping("/h5/api")
@RestController
@Slf4j
public class DeviceController extends BaseController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private DeviceTeamService deviceTeamService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 首页查询我的设备
     * @return
     */
    @RequestMapping("/obtainMyDevice")
    public ApiResponse<DeviceListVo> obtainMyDevice() {
        Integer userId = getCurrentUserId();
        log.info("查询我的设备列表，userId={}", userId);
        DeviceListVo deviceListVo = deviceService.obtainMyDevice(userId);
        return new ApiResponse<>(deviceListVo);
    }

    /**
     * 查询子设备
     * @return
     */
    @RequestMapping("/obtainChildDevice/{hostDeviceId}")
    public Object obtainChildDevice(@PathVariable("hostDeviceId") Integer hostDeviceId) {
        Integer userId = getCurrentUserId();
        log.info("查询子设备列表，userId={}， hostDeviceId={}", userId, hostDeviceId);
        List<DeviceListVo.DeviceItemPo> childDeviceList = deviceService.queryChildDevice(hostDeviceId);
        return new ApiResponse<>(childDeviceList);
    }

    /**
     * 暂时不用
     * @return
     */
    @RequestMapping("/queryDetailByDeviceId")
    public ApiResponse<DeviceDetailVo> queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = deviceDataService.queryDetailByDeviceId(deviceId);
        return new ApiResponse<>(deviceDetailVo);
    }

    @RequestMapping("/getLocation/{deviceId}")
    public ApiResponse<LocationVo> queryDeviceLocation(@PathVariable("deviceId") Integer deviceId) {
        LocationVo locationVo = deviceService.queryDeviceLocation(deviceId);
        return new ApiResponse<>(locationVo);
    }

    @RequestMapping("/getWeather/{deviceId}")
    public ApiResponse<WeatherVo> queryDeviceWeather(@PathVariable("deviceId") Integer deviceId) {
        WeatherVo weatherVo = deviceService.queryDeviceWeather(deviceId);
        return new ApiResponse<>(weatherVo);
    }

    /**
     * 新版首页查询我的设备
     * @return
     */
    @RequestMapping("/newQueryDetailByDeviceId")
    public ApiResponse<List<DeviceAbilitysVo>> newQueryDetailByDeviceId(@RequestBody DeviceAbilitysRequest request) {
        Integer deviceId = request.getDeviceId();
        List<Integer> abilityIds = request.getAbilityIds();
        if(deviceId == null || abilityIds.isEmpty()){
            return new ApiResponse<>(RetCode.PARAM_ERROR, "设备功能不能为空");
        }
        List<DeviceAbilitysVo> deviceAbilityVos = deviceDataService.queryDetailAbilitysValue(deviceId, abilityIds);
        return new ApiResponse<>(deviceAbilityVos);
    }

    @RequestMapping("/editDevice")
    public ApiResponse<Boolean> editDevice(@RequestBody DeviceRequest request) {
        Integer deviceId = request.getDeviceId();
        Integer userId = getCurrentUserId();
        String deviceName = request.getDeviceName();
        log.info("编辑设备，deviceId={}，deviceName={}，userId={}", deviceId, deviceName, userId);
        boolean ret = deviceService.editDevice(userId, deviceId, deviceName);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/token")
    public ApiResponse<String> obtainShareToken(@RequestBody BaseRequest<String> request) {
        String wxDeviceId = request.getValue();
        String lastToken = stringRedisTemplate.opsForValue().get("token." + wxDeviceId);
        if (StringUtils.isNotEmpty(lastToken)) {
            return new ApiResponse<>(lastToken);
        }
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        stringRedisTemplate.opsForValue().set("token." + wxDeviceId, token);
        stringRedisTemplate.expire("token." + wxDeviceId, 10, TimeUnit.MINUTES);
        return new ApiResponse<>(token);
    }

    @RequestMapping("/share")
    public Object shareDevice(@RequestBody ShareRequest request) throws InvocationTargetException, IllegalAccessException {
    //public Object shareDevice(HttpServletRequest request, String masterOpenId, String deviceId, String token) {
        Integer userId = getCurrentUserId();

        Object shareOk = deviceDataService.shareDevice(userId, request);
        return new ApiResponse<>(shareOk);
    }

    @RequestMapping("/deleteDevice")
    public ApiResponse<Boolean> deleteDevice(@RequestBody BaseRequest<Integer> request){
        Integer deviceId = request.getValue();
        Integer userId = getCurrentUserId();
        log.info("删除设备，deviceId={}，userId={}", deviceId, userId);
        Boolean ret = deviceDataService.deleteDevice(userId,deviceId);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/clearRelation")
    public ApiResponse<Boolean> clearRelation(@RequestBody ClearShareRequest request) {
        Integer userId = getCurrentUserId();
        Integer deviceId = request.getDeviceId();
        String openId = request.getOpenId();
        log.info("删除用户管理设备权限，删除人userId={}, 设备Id={}, 被删除用户openId={}", userId, deviceId, openId);
        Boolean clearOk = deviceDataService.clearRelation(openId, userId, deviceId);
        return new ApiResponse<>(clearOk);
    }

    @RequestMapping("/updateDeviceLocation")
    public ApiResponse<Boolean> updateDeviceLocation(@RequestBody DeviceLocationRequest request){
        Integer deviceId = request.getDeviceId();
        String location = request.getLocation();
        if(StringUtils.isEmpty(location) || deviceId == null){
            return new ApiResponse(RetCode.PARAM_ERROR, "参数错误");
        }
        Integer userId = getCurrentUserId();
        Boolean clearOk = deviceService.editDeviceLoc(userId, deviceId,location);
        return new ApiResponse<>(clearOk);
    }

    @RequestMapping("/shareList")
    public ApiResponse<List<DeviceShareVo>> shareList(@RequestBody BaseRequest<Integer> request) {
        Integer userId = getCurrentUserId();
        Integer deviceId = request.getValue();
        log.info("查询共享权限列表，userId={}, deviceId={}", userId, deviceId);
        List<DeviceShareVo> deviceShareVos = deviceDataService.shareList(userId, deviceId);
        return new ApiResponse<>(deviceShareVos);
    }

    @RequestMapping("/sendFunc")
    public ApiResponse<String> sendFunc(@RequestBody DeviceFuncVo deviceFuncVo) {
        String funcId = deviceFuncVo.getFuncId();
        String requestId = deviceDataService.sendFunc(deviceFuncVo,getCurrentUserId(),1);
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

    @GetMapping("/getHistoryData")
    public ApiResponse<List<SensorDataVo>> getHistoryData(Integer deviceId, Integer type) {
        return new ApiResponse<>(deviceDataService.getHistoryData(deviceId, type));

    }

}