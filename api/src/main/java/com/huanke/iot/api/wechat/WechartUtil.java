package com.huanke.iot.api.wechat;

import com.alibaba.fastjson.JSON;
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
import java.util.concurrent.TimeUnit;

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
    @Value("${accessTokenKey}")
    private String accessTokenKey;

    @Value("${ticketKey}")
    private String ticketKey;

    @Value("${jsapiKey}")
    private String jsapiKey;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getAccessToken(boolean getFromSever) {
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
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                log.info("result = {}", result);
                JSONObject json = JSONObject.parseObject(result.toString());
                if (json.containsKey("access_token")) {
                    String queryAccessToken = json.getString("access_token");
                    if (StringUtils.isNotEmpty(queryAccessToken)) {
                        stringRedisTemplate.opsForValue().set(accessTokenKey, queryAccessToken);
                        return queryAccessToken;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    public JSONObject obtainAuthAccessToken(String code){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
        log.info("obtainAuthAccessToken url = {}",url);
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
           if(jsonObject != null){
                if(jsonObject.containsKey("access_token")){
                    return jsonObject;
                }
           }
        }catch (Exception e){

        }
        return null;
    }

    public  String getJsApi(){
        String jsapi  = stringRedisTemplate.opsForValue().get(jsapiKey);
        if(StringUtils.isNotEmpty(jsapi)){
            return jsapi;
        }
        String apiResult = obtainRemoteJsapi();
        log.info("apiResult result = {}", apiResult);
        JSONObject json = JSONObject.parseObject(apiResult);
        int errorCode = json.getInteger("errcode");
        if (errorCode == 42001) {
            getAccessToken(true);
            apiResult = obtainRemoteTicket();
            json = JSONObject.parseObject(apiResult);
        }
        if (json.containsKey("ticket")) {
            String queryTicket = json.getString("ticket");
            if (StringUtils.isNotEmpty(queryTicket)) {
                stringRedisTemplate.opsForValue().set(ticketKey, queryTicket);
                stringRedisTemplate.expire(ticketKey, 7000,TimeUnit.SECONDS);

                return queryTicket;
            }
        }
        return "";
    }

    private String obtainRemoteJsapi() {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getAccessToken(false) + "&type=jsapi";
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
            return "";
        }
    }

    public JSONObject obtainUserUserInfo(String accessToken,String openId){
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken+"&openid="+openId+"&lang=zh_CN";
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
            log.info("result = {}",result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            if(jsonObject != null){
                if(!jsonObject.containsKey("errcode")){
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public Boolean isAccessTokenOk(String accessToken,String openId){
        String url = "https://api.weixin.qq.com/sns/auth?access_token="+accessToken+"&openid="+openId+"&lang=zh_CN";
        try {
            log.info("url = {}",url);
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
            log.info("result = {}",result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            if(jsonObject != null){
                int errcode = jsonObject.getInteger("errcode");
                return errcode == 0;
            }
            return false;
        } catch (Exception e) {
            return null;
        }
    }

    private String obtainRemoteTicket() {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getAccessToken(false) + "&type=wx_card";
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
            return "";
        }
    }

    public JSONObject getByRefreshToken(String refresh_token) {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appId+"&grant_type=refresh_token&refresh_token="+refresh_token;
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
            log.info("getByRefreshToken result = {}",result.toString());
            JSONObject jsonObject = JSON.parseObject(result.toString());
            return  jsonObject;
        } catch (Exception e) {
            return null;
        }
    }
}
