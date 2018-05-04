package com.huanke.iot.api.controller.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.app.request.AppFuncVo;
import com.huanke.iot.api.controller.app.response.AppDeviceDataVo;
import com.huanke.iot.api.controller.app.response.AppInfoVo;
import com.huanke.iot.api.controller.app.response.VideoVo;
import com.huanke.iot.api.controller.h5.BaseController;
import com.huanke.iot.api.controller.h5.req.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.api.controller.h5.response.SensorDataVo;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.api.service.device.basic.DeviceService;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.user.AppUserPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;


@RequestMapping("/app/api")
@Slf4j
@RestController
public class AppController extends BaseController {

    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private AppUserMapper appUserMapper;

    @Value("${gameServerHost}")
    private String gameServerHost;
    @Value("${appId}")
    private String appId;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceService deviceService;

    @RequestMapping("/bind")
    public ApiResponse<Boolean> userAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imei = request.getParameter("IMEI");
        if (StringUtils.isEmpty(imei)) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "mac地址不能为空");
        }
        AppUserPo appUserPo = appUserMapper.selectByMac(imei);
        if (appUserPo == null) {
            String code = request.getParameter("code");
            if (StringUtils.isEmpty(code)) {
                String redirectUrl = gameServerHost + "/api/app/api/bind?IMEI=" + imei;
                try {
                    redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
                } catch (Exception e) {

                }
                String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + redirectUrl +
                        "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
                response.sendRedirect(url);
                return null;
            }
            JSONObject authTokenJSONObject = wechartUtil.obtainAuthAccessToken(code);
            if (authTokenJSONObject == null) {
                return new ApiResponse<>(RetCode.CODE_ERROR, "获取用户的accessToken错误");
            }
            String openId = authTokenJSONObject.getString("openid");
            if (StringUtils.isEmpty(openId)) {
                return new ApiResponse<>(RetCode.CODE_ERROR, "获取openId错误");
            }
            AppUserPo updateAppUserPo = new AppUserPo();
            updateAppUserPo.setOpenId(openId);
            updateAppUserPo.setAndroidMac(imei);
            int ret = appUserMapper.updateAndroidMac(updateAppUserPo);
            if (ret == 0) {
                return new ApiResponse<>(false);
            }
        }

        return new ApiResponse<>(true);
    }

    @RequestMapping("/queryDeviceList")
    public ApiResponse<DeviceListVo> queryDeviceList(HttpServletRequest request) {
        Integer userId = getCurrentUserIdForApp(request);
        DeviceListVo deviceListVo = deviceService.obtainMyDevice(userId);
        return new ApiResponse<>(deviceListVo);
    }

    @RequestMapping("/queryDetailByDeviceId")
    public ApiResponse<DeviceDetailVo> queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = deviceDataService.queryDetailByDeviceId(deviceId);
        return new ApiResponse<>(deviceDetailVo);
    }

    @RequestMapping("/getHistoryData")
    public ApiResponse<List<SensorDataVo>> getHistoryData(String deviceId, Integer type) {

        return new ApiResponse<>(deviceDataService.getHistoryData(deviceId, type));
    }

    @RequestMapping("/editDevice")
    public ApiResponse<Boolean> editDevice(HttpServletRequest request, String deviceId, String deviceName) {
        Integer userId = getCurrentUserIdForApp(request);
        boolean ret = deviceService.editDevice(userId, deviceId, deviceName);
        return new ApiResponse<>(ret);
    }


    @RequestMapping("/sendFunc")
    public ApiResponse<String> sendFunc(@RequestBody DeviceFuncVo deviceFuncVo) {
        String requestId = deviceDataService.sendFunc(deviceFuncVo);
        return new ApiResponse<>(requestId);
    }

    @RequestMapping("/obtainApk")
    public ApiResponse<AppInfoVo> obtainApk() {

        String apkInfo = stringRedisTemplate.opsForValue().get("apkInfo");
        if (StringUtils.isNotEmpty(apkInfo)) {
            AppInfoVo appInfoVo = JSON.parseObject(apkInfo, AppInfoVo.class);
            return new ApiResponse<>(appInfoVo);
        }
        return new ApiResponse<>();

    }

    @RequestMapping("/sendFunc")
    public ApiResponse<Boolean> sendFuc(@RequestBody AppFuncVo appFuncVo){
        return new ApiResponse<>(true);
    }

    @RequestMapping("/setApkInfo")
    public ApiResponse<Boolean> setApkInfo(String v, String u) {
        AppInfoVo appInfoVo = new AppInfoVo();
        appInfoVo.setApkUrl(u);
        appInfoVo.setCurrentVersion(v);
        stringRedisTemplate.opsForValue().set("apkInfo", JSON.toJSONString(appInfoVo));
        return new ApiResponse<>(true);
    }
}
