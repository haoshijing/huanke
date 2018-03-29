package com.huanke.iot.api.wechart.js.controller;

import com.huanke.iot.api.wechart.js.JsApiConfig;
import com.huanke.iot.api.wechart.js.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WechatConrtoller {

    @Autowired
    WechartUtil wechartUtil;
    @RequestMapping("/obtainJsConfig")
    public ApiResponse<String> obtainJsConfig(){
        JsApiConfig jsApiConfig  = new JsApiConfig();
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
