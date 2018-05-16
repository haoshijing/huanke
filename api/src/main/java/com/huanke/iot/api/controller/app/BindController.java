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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @author haoshijing
 * @version 2018年05月16日 10:01
 **/
@Controller
@Slf4j
@RequestMapping("/app/api")
public class BindController {

    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private AppUserMapper appUserMapper;

    @Value("${gameServerHost}")
    private String gameServerHost;
    @Value("${appId}")
    private String appId;
    @RequestMapping("/bind")
    public String userAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imei = request.getParameter("IMEI");
        if (StringUtils.isEmpty(imei)) {
            return "fail";
        }
        AppUserPo appUserPo = appUserMapper.selectByMac(imei);
        if (appUserPo == null) {
            String code = request.getParameter("code");
            if (StringUtils.isEmpty(code)) {
                String redirectUrl = gameServerHost + "/api/app/api/bind?IMEI=" + imei;
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
                return "fail";
            }
            String openId = authTokenJSONObject.getString("openid");
            if (StringUtils.isEmpty(openId)) {
                return "fail";
            }
            AppUserPo updateAppUserPo = new AppUserPo();
            updateAppUserPo.setOpenId(openId);
            updateAppUserPo.setAndroidMac(imei);
            int ret = appUserMapper.updateAndroidMac(updateAppUserPo);
            if (ret == 0) {
                return "fail";
            }
        }
        return "succ";
    }

}
