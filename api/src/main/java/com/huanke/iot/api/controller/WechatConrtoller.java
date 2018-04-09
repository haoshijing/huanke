package com.huanke.iot.api.controller;

import com.huanke.iot.api.util.Md5Util;
import com.huanke.iot.api.JsApiConfig;
import com.huanke.iot.api.wechat.WechartUtil;
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

    @Value("${appId}")
    private String appId;
    @Value("${appSecret}")
    private String appSecret;

    @Autowired
    private WechartUtil wechartUtil;

    @RequestMapping("/obtainJsConfig")
    public ApiResponse<JsApiConfig> obtainJsConfig(String link){
        JsApiConfig jsApiConfig  = new JsApiConfig();
        jsApiConfig.setAppId(appId);
        jsApiConfig.setNonce(UUID.randomUUID().toString().replace("-",""));
        jsApiConfig.setTimestamp(String.valueOf(System.currentTimeMillis()));
        jsApiConfig.setLink(link);
        String ticket = wechartUtil.getTicket();
        String src = "jsapi_ticket=" + ticket + "&noncestr="
                + jsApiConfig.getNonce() + "&timestamp=" + jsApiConfig.getTimestamp() + "&url="
                + jsApiConfig.getLink();
        try {
            String sign = Md5Util.sha1(src);
            jsApiConfig.setSignature(sign);
        }catch (Exception e){

        }
        return new ApiResponse<>(jsApiConfig);
    }

    @RequestMapping("/obtainTicket")
    public ApiResponse<String> obtainTicket(){
        String ticket = wechartUtil.getTicket();
        log.info("ticket="+ticket);
        return new ApiResponse<>(ticket);
    }

    @RequestMapping("/queryByMac")
    public String queryDeviceByMac(String mac){
        log.info("mac = {}",mac);
        return "001";
    }
}
