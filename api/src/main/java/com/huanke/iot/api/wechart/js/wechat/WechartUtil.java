package com.huanke.iot.api.wechart.js.wechat;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * @author haoshijing
 * @version 2018年03月29日 16:39
 **/
@Repository
@Slf4j
public class WechartUtil {
    @Value("${appId}")
    private String appId;
    @Value("${appSecret}")
    private String appSecret;

    private static final String ACCESS_TOKEN = "access_token";

    private static final String TICKET = "ticket";

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
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(new URI(url));
                CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                log.info("result = {}", result);
                JSONObject json = JSONObject.parseObject(result.toString());
                if (json.containsKey("access_token")) {
                    String queryAccessToken = json.getString("access_token");
                    if (StringUtils.isNotEmpty(queryAccessToken)) {
                        stringRedisTemplate.opsForValue().set(ACCESS_TOKEN, queryAccessToken, 5400);
                        return queryAccessToken;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public String getTicket() {
        String ticket = stringRedisTemplate.opsForValue().get(TICKET);
        if (false && StringUtils.isNotEmpty(ticket)) {
            return ticket;
        }
        String ticketResult = obtainRemoteTicket();

        log.info("ticket result = {}", ticketResult);
        JSONObject json = JSONObject.parseObject(ticketResult);
        int errorCode = json.getInteger("errcode");
        if (errorCode == 42001) {
            getAccessToken(true);
            ticketResult = obtainRemoteTicket();
            json = JSONObject.parseObject(ticketResult);
        }
        if (json.containsKey("ticket")) {
            String queryTicket = json.getString("ticket");
            if (StringUtils.isNotEmpty(queryTicket)) {
                stringRedisTemplate.opsForValue().set(TICKET, queryTicket, 5200);
                return queryTicket;
            }
        }

        return "";
    }

    private String obtainRemoteTicket() {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getAccessToken(false) + "&type=wx_card";
        try {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            return "";
        }
    }

}
