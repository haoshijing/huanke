package com.huanke.iot.api.controller.app.response;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * @author haoshijing
 * @version 2018年04月27日 12:06
 **/
@Data
public class AppInfoVo {
    private String versionCode = "";
    private String apkUrl;
    private String versionName = "";

    public static void main(String[] args) {
        AppInfoVo appInfoVo = new AppInfoVo();
        appInfoVo.setApkUrl("http:/xxxxx");
        appInfoVo.setVersionCode("1");
        appInfoVo.setVersionName("测试版本");
        System.out.println(JSON.toJSONString(appInfoVo));

        Jedis jedis = new Jedis(new JedisShardInfo("39.104.96.141",8379));
        jedis.set("dash.apk",JSON.toJSONString(appInfoVo));

    }
}
