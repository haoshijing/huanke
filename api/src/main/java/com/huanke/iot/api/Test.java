package com.huanke.iot.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;

public class Test {
    public static void main(String[] args) throws Exception {
       String url = "https://api.weixin.qq.com/sns/auth?access_token=" +
               "8_CmM8mVG70ehISPjJxfNJorqKu2s7JGTVk61UJ-_FBJKCco6WbjjZEvg4g0ZlxTkb1WVruvXUaYklZ3chW2VzkA&openid=okOTjwievhXr-wh8IRMk9J79gioc&lang=zh_CN";
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
            System.out.println("args = [" + result.toString() + "]");
        }catch (Exception e){

        }
    }
}
