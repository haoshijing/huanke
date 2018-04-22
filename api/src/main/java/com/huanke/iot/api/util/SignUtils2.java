package com.huanke.iot.api.util;

import com.google.common.collect.Maps;
import com.huanke.iot.api.wechat.WechartUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;
import java.util.UUID;


@Repository
@Slf4j
public class SignUtils2 {
    @Autowired
    private WechartUtil wechartUtil;

    public  Map<String, String> sign(String url) {

        String jsapi_ticket = wechartUtil.getJsApi();

        Map<String, String> ret = Maps.newHashMap();

        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp + "&url=" + url;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }catch (NoSuchAlgorithmException e){
        }catch (UnsupportedEncodingException e){
            log.error("",e);
        }
        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        return ret;

    }
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
