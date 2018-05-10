package com.huanke.iot.api.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.user.AppUserPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

@RequestMapping("/app/api")
@Slf4j
@Controller
public class BindController {
    @Autowired
    private WechartUtil wechartUtil;

    @Value("${gameServerHost}")
    private String gameServerHost;

    @Value("${appId}")
    private String appId;

    @Autowired
    private AppUserMapper appUserMapper;
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
                String redirectUrl = gameServerHost + "/app/api/bind?IMEI=" + imei;
                try {
                    redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
                } catch (Exception e) {
                    log.error("",e);
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
        response.sendRedirect("http://www.baidu.com");
        return new ApiResponse<>(true);
    }
}
