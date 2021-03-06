package com.huanke.iot.manage.service.wechart;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class WechartUtil {
    private static final String ACCESSS_TOKEN_PREIX = "accessTokenKey.customerId.";
    private static final String JSAPI_PREIX = "jsapi.";
    private static final String TICKET_PREFIX = "ticketKey.";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getAccessToken(String appId, String appSecret, String customerId, boolean getFromSever) {
        String accessTokenKey = ACCESSS_TOKEN_PREIX + customerId;
        boolean needFromServer = getFromSever;
        if (!needFromServer) {
            String storeAccessToken = stringRedisTemplate.opsForValue().get(accessTokenKey);
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
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                JSONObject json = JSONObject.parseObject(result.toString());
                log.info("result = {}", result);
                if (json.containsKey("access_token")) {
                    String queryAccessToken = json.getString("access_token");
                    if (StringUtils.isNotEmpty(queryAccessToken)) {
                        stringRedisTemplate.opsForValue().set(accessTokenKey, queryAccessToken);
                        stringRedisTemplate.expire(accessTokenKey, 7000, TimeUnit.SECONDS);
                        return queryAccessToken;
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return "";
    }

    public JSONObject obtainAuthAccessToken(String appId, String appSecret, String customerId, String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
        log.info("obtainAuthAccessToken url = {}", url);
        try {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.info("result = {}", result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            if (jsonObject != null) {
                if (jsonObject.containsKey("access_token")) {
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public String getJsApi(String appId, String appSecret, String customerId) {
        String jsapiKey = JSAPI_PREIX + customerId;
        String ticketKey = TICKET_PREFIX + customerId;
        String jsapi = stringRedisTemplate.opsForValue().get(jsapiKey);
        if (StringUtils.isNotEmpty(jsapi)) {
            return jsapi;
        }
        String apiResult = obtainRemoteJsapi(appId, appSecret, customerId);
        log.info("apiResult result = {}", apiResult);
        JSONObject json = JSONObject.parseObject(apiResult);
        int errorCode = json.getInteger("errcode");
        if (errorCode == 42001) {
            getAccessToken(appId, appSecret, customerId, true);
            apiResult = obtainRemoteTicket(appId, appSecret, customerId);
            json = JSONObject.parseObject(apiResult);
        }
        if (json.containsKey("ticket")) {
            String queryTicket = json.getString("ticket");
            if (StringUtils.isNotEmpty(queryTicket)) {
                stringRedisTemplate.opsForValue().set(ticketKey, queryTicket);
                stringRedisTemplate.expire(ticketKey, 7000, TimeUnit.SECONDS);

                return queryTicket;
            }
        }
        return "";
    }

    private String obtainRemoteJsapi(String appId, String appSecret, String customerId) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" +
                getAccessToken(appId, appSecret, customerId, false) + "&type=jsapi";
        try {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            log.error("", e);
            return "";
        }
    }

    public JSONObject obtainUserUserInfo(String accessToken, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        try {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.info("result = {}", result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            if (jsonObject != null) {
                if (!jsonObject.containsKey("errcode")) {
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
        return null;
    }

    public Boolean isAccessTokenOk(String accessToken, String openId) {
        String url = "https://api.weixin.qq.com/sns/auth?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        try {
            log.info("url = {}", url);
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.info("result = {}", result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            if (jsonObject != null) {
                int errcode = jsonObject.getInteger("errcode");
                return errcode == 0;
            }
            return false;
        } catch (Exception e) {
            return null;
        }
    }

    private String obtainRemoteTicket(String appId, String appSecret, String customerId) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getAccessToken(appId, appSecret, customerId, false) + "&type=wx_card";
        try {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            log.error("", e);
            return "";
        }
    }

    public JSONObject getByRefreshToken(String appId, String refresh_token) {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + appId + "&grant_type=refresh_token&refresh_token=" + refresh_token;
        try {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.info("getByRefreshToken result = {}", result.toString());
            JSONObject jsonObject = JSON.parseObject(result.toString());
            return jsonObject;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }
}
