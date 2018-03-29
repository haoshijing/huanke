package com.huanke.iot.api.wechart.js.wechat;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author haoshijing
 * @version 2018年03月29日 16:39
 **/
@Repository
public class WechartUtil {
    @Value("${appId}")
    private String appId;
    @Value("${appSecret}")
    private String appSecret;

    @Autowired
    private HttpClient httpClient;

    private static final String ACCESS_TOKEN = "access_token";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getAccessToken(boolean getFromSever) {
        boolean needFromServer = getFromSever;
        if (!needFromServer) {
            String storeAccessToken = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN);
            if (StringUtils.isNotEmpty(storeAccessToken)) {
                return storeAccessToken;
            }
            needFromServer = true;
        }
        if (needFromServer) {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
            try {
                String returnData = httpClient.newRequest(url).send().getContentAsString();
                JSONObject json = JSONObject.parseObject(returnData);
                if (json.containsKey("access_token")) {
                    String queryAccessToken = json.getString("access_token");
                    if (StringUtils.isNotEmpty(queryAccessToken)) {
                        stringRedisTemplate.opsForValue().set(ACCESS_TOKEN,queryAccessToken,5400);
                        return queryAccessToken;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
