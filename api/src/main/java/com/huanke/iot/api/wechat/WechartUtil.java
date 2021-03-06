package com.huanke.iot.api.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author haoshijing
 * @version 2018年03月29日 16:39
 **/
@Repository
@Slf4j
public class WechartUtil {
    private static final String ACCESSS_TOKEN_PREIX = "accessTokenKey.customerId.";
    private static final String JSAPI_PREIX = "jsapi.";
    private static final String TICKET_PREFIX = "ticketKey.";

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getAccessToken(boolean getFromSever) {
        UserRequestContext context =  UserRequestContextHolder.get();
        Integer customerId = context.getCustomerVo().getCustomerId();
        CustomerPo customerPo = customerMapper.selectById(customerId);
        String appId = customerPo.getAppid();
        String appSecret = customerPo.getAppsecret();
        String accessTokenKey = ACCESSS_TOKEN_PREIX+getCurrentCustomerId();
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
                        stringRedisTemplate.expire(accessTokenKey, 7000, TimeUnit.SECONDS);
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
        UserRequestContext context = UserRequestContextHolder.get();
        Integer customerId = context.getCustomerVo().getCustomerId();
        CustomerPo customerPo = customerMapper.selectById(customerId);
        String appId = customerPo.getAppid();
        String appSecret = customerPo.getAppsecret();
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
        String jsapiKey = JSAPI_PREIX+getCurrentCustomerId();
        String ticketKey  = TICKET_PREFIX + getCurrentCustomerId();
        String jsapi  = stringRedisTemplate.opsForValue().get(ticketKey);
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
        if(!json.containsKey("ticket")){
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
        UserRequestContext context =  UserRequestContextHolder.get();
        Integer customerId = context.getCustomerVo().getCustomerId();
        CustomerPo customerPo = customerMapper.selectById(customerId);
        String appId = customerPo.getAppid();
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

    private String getCurrentPublicId(){
        UserRequestContext context =  UserRequestContextHolder.get();
        Integer customerId = context.getCustomerVo().getCustomerId();
        CustomerPo customerPo = customerMapper.selectById(customerId);
        String publicId = customerPo.getPublicId();
        return publicId;
    }

    private Integer getCurrentCustomerId(){
        UserRequestContext context =  UserRequestContextHolder.get();
        Integer customerId = context.getCustomerVo().getCustomerId();
        return customerId;
    }

    public List<String> obtainMyDeviceIdList(String openId){
        List<String> deviceIdList = new ArrayList<>();
        String accessToken = this.getAccessToken(false);
        String url = "https://api.weixin.qq.com/device/get_bind_device?access_token=" + accessToken + "&openid=" + openId;
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
            JSONArray deviceJsonList = jsonObject.getJSONArray("device_list");

            for(int i=0; i<deviceJsonList.size(); i++){
                Map<String, Object> itemMap = JSONObject.toJavaObject(deviceJsonList.getJSONObject(i), Map.class);
                String deviceId = (String) itemMap.get("device_id");
                deviceIdList.add(deviceId);
            }
            return deviceIdList;
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean unbindDevice(String openId, String wxDeviceId) {
        List<String> deviceIdList = new ArrayList<>();
        String accessToken = this.getAccessToken(false);
        String url = "https://api.weixin.qq.com/device/compel_unbind?access_token=" + accessToken;
        HttpResponse httpResponse = null;
        ArrayList<NameValuePair> postParameters;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");
            JSONObject json = new JSONObject();
            json.put("device_id", wxDeviceId);
            json.put("openid", openId);
            HttpEntity e = new StringEntity(json.toString());
            httpPost.setEntity(e);
            httpResponse = httpclient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString
                        (httpResponse.getEntity());
                JSONObject resultJsonObject = JSONObject.parseObject(result);
                if(resultJsonObject.getJSONObject("base_resp").getInteger("errcode") == 0){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
