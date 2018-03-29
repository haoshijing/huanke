package com.huanke.iot.api.wechart.js.controller;

import com.huanke.iot.api.wechart.js.JsApiConfig;
import com.huanke.iot.api.wechart.js.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoshijing
 * @version 2018年03月29日 16:14
 **/
@RestController
public class WechatConrtoller {

    @Autowired
    WechartUtil wechartUtil;
    @RequestMapping("/obtainJsConfig")
    public ApiResponse<JsApiConfig> obtainJsConfig(){
        JsApiConfig jsApiConfig  = new JsApiConfig();
        String token = wechartUtil.getAccessToken(false);
        System.out.println("token="+token);
        return new ApiResponse<>(jsApiConfig);
    }
}
