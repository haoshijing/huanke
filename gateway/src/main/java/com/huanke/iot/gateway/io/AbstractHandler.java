package com.huanke.iot.gateway.io;

import com.google.common.collect.Maps;
import com.huanke.iot.gateway.onlinecheck.OnlineCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:17
 **/
@Slf4j
public abstract  class AbstractHandler {

    private static  ConcurrentMap<String,AbstractHandler> handlerMap= Maps.newConcurrentMap();
    @Autowired
    private OnlineCheckService onlineCheckService;
    @PostConstruct
    public void init(){
        handlerMap.putIfAbsent(getTopicType(),this);
    }
    protected abstract String getTopicType();


    public  void  handler(String topic, byte[] payloads){
        Integer id = getDeviceIdFromTopic(topic);
        log.info("id = {} receive data",id);
        onlineCheckService.resetOnline(id);
        doHandler(topic,payloads);
    }
    public abstract void  doHandler(String topic, byte[] payloads);

    public static AbstractHandler getHandler(String topic){
        String type = getTypeFromTopic(topic);
        return handlerMap.get(type);
    }

    protected Integer getDeviceIdFromTopic(String topic){
        int idx = topic.lastIndexOf("/");
        Integer deviceId =  Integer.valueOf(topic.substring(idx+1));
        return deviceId;
    }
    private static String getTypeFromTopic(String topic){
        int idx = topic.lastIndexOf("/");
        String dataStr = topic.substring(0,idx);
        idx = dataStr.lastIndexOf("/");
        return dataStr.substring(idx+1);
    }

}
