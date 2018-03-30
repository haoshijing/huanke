package com.huanke.iot.api.wechart.js.controller;

import com.huanke.iot.api.wechart.js.JsApiConfig;
import com.huanke.iot.api.wechart.js.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author haoshijing
 * @version 2018年03月29日 16:14
 **/
@RestController
@Slf4j
public class WechatConrtoller {

    @Autowired
    WechartUtil wechartUtil;

    @Value("${appId}")
    private String appId;
    @Value("${appSecret}")
    private String appSecret;

    @RequestMapping("/obtainJsConfig")
    public ApiResponse<String> obtainJsConfig(String link){
        JsApiConfig jsApiConfig  = new JsApiConfig();
        jsApiConfig.setAppId(appId);
        jsApiConfig.setNonce(UUID.randomUUID().toString().replace("-",""));
        jsApiConfig.setTimestamp(String.valueOf(System.currentTimeMillis()));
        jsApiConfig.setLink(link);
        String token = wechartUtil.getAccessToken(false);
        log.info("token="+token);
        return new ApiResponse<>(token);
    }

    @RequestMapping("/obtainTicket")
    public ApiResponse<String> obtainTicket(){
        String ticket = wechartUtil.getTicket();
        log.info("ticket="+ticket);
        return new ApiResponse<>(ticket);
    }
}
