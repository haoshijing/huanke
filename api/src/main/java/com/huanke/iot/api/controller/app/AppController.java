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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;


@RequestMapping("/app/api")
@Slf4j
@RestController
public class AppController {

    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private AppUserMapper appUserMapper;

    @Value("${gameServerHost}")
    private String gameServerHost;
    @Value("${appId}")
    private String appId;


    @RequestMapping("/bind")
    public ApiResponse<Boolean> userAuth(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String mac = request.getParameter("mac");
        if(StringUtils.isEmpty(mac)){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"mac地址不能为空");
        }
        AppUserPo appUserPo = appUserMapper.selectByMac(mac);
        if(appUserPo == null){
            String code = request.getParameter("code");
            if(StringUtils.isEmpty(code)){
                String redirectUrl = gameServerHost+"/app/api/bind?mac="+mac;
                try{
                    redirectUrl = URLEncoder.encode(redirectUrl,"UTF-8");
                }catch (Exception e){

                }
                response.sendRedirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+redirectUrl+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
                return null ;
            }
            JSONObject authTokenJSONObject = wechartUtil.obtainAuthAccessToken(code);
            if(authTokenJSONObject == null){
                return new ApiResponse<>(RetCode.CODE_ERROR,"获取用户的accessToken错误");
            }
            String openId = authTokenJSONObject.getString("openid");
            if(StringUtils.isEmpty(openId)){
                return new ApiResponse<>(RetCode.CODE_ERROR,"获取openId错误");
            }
            AppUserPo updateAppUserPo = new AppUserPo();
            updateAppUserPo.setOpenId(openId);
            updateAppUserPo.setAndroidMac(mac);
            int ret = appUserMapper.updateAndroidMac(updateAppUserPo);
            if(ret == 0){
                return new ApiResponse<>(false);
            }
        }

        return new ApiResponse<>(true);
    }

}
