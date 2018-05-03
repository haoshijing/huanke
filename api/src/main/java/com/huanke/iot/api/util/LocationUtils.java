package com.huanke.iot.api.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

@Service
public class LocationUtils {

    public JSONObject getLocation(String ip) {
        if (StringUtils.isNotEmpty(ip)) {
            return null;
        }
        String url = String.format("http://api.map.baidu.com/location/ip?ak=omi69HPHpl5luMtrjFzXn9df&ip=%s&coor=bd09ll", ip);
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
            String ret = result.toString();
            JSONObject jsonObject = JSON.parseObject(ret);
            return jsonObject;
        } catch (Exception e) {

        }
        return null;
    }

    public JSONObject getWeather(String ip) {
        if (StringUtils.isNotEmpty(ip)) {
            return null;
        }
        String url = String.format("http://api.k780.com:88/?app=weather.today&weaid=%s&appkey=33302&sign=96b4d475536090dac84ff04daa37d785&format=json",
                ip);
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
            String ret = result.toString();
            JSONObject jsonObject = JSON.parseObject(ret);
            return jsonObject;
        } catch (Exception e) {

        }
        return null;
    }
}
