package com.huanke.iot.api.controller.h5;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.api.service.user.UserService;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

@RestController
@RequestMapping("/h5/api")
@Slf4j
public class AuthController {

    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private UserService userService;


    @RequestMapping("/user/auth")
    public ApiResponse<String> userAuth(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String code = request.getParameter("code");
        UserRequestContext userRequestContext = UserRequestContextHolder.get();
        String appId = userRequestContext.getCacheVo().getAppId();
        if(StringUtils.isEmpty(code)){
            String redirect_uri = request.getRequestURL()+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
            String fullRedirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid"+appId+"&redirect_uri="
                    + URLEncoder.encode(redirect_uri, "UTF-8");
            response.sendRedirect(fullRedirectUrl);
            return null;
        }
        JSONObject authTokenJSONObject = wechartUtil.obtainAuthAccessToken(code);
        if(authTokenJSONObject == null){
            return new ApiResponse<>(RetCode.CODE_ERROR,"获取用户的accessToken错误");
        }
        String openId = authTokenJSONObject.getString("openid");
        String access_token = authTokenJSONObject.getString("access_token");
        String refresh_token = authTokenJSONObject.getString("refresh_token");
        Boolean isOk = wechartUtil.isAccessTokenOk(access_token,openId);
        if(!isOk) {
            authTokenJSONObject = wechartUtil.getByRefreshToken(refresh_token);
            openId = authTokenJSONObject.getString("openid");
            access_token = authTokenJSONObject.getString("access_token");
            isOk = wechartUtil.isAccessTokenOk(access_token,openId);
            log.info("isOk = {}",isOk);
        }
        if(isOk){
            userService.addOrUpdateUser(access_token,openId);
        }
        return new ApiResponse<>(openId);
    }

}
