package com.huanke.iot.gateway.redis;

import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

/**
 * @author haoshijing
 * @version 2018年03月28日 12:21
 **/
public class RedisClientTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        RedisOptions config = new RedisOptions()
                .setHost("115.159.29.17")
                .setPort(8379);

        RedisClient redis = RedisClient.create(vertx, config);

        redis.set("test","value",handler->{
           if(handler.succeeded()){
               handler.result();
               System.out.println("发送成功");
           }
        });

        redis.get("test",handler->{
            System.out.println(Thread.currentThread().getName()+handler.result());
        });

    }
}
