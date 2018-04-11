package com.huanke.iot.gateway.io;

import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:17
 **/
public abstract  class AbstractHandler {

    private static  ConcurrentMap<String,AbstractHandler> handlerMap= Maps.newConcurrentMap();

    @PostConstruct
    public void init(){
        handlerMap.putIfAbsent(getTopicType(),this);
    }
    protected abstract String getTopicType();


    public abstract void  doHandler(String topic, byte[] payloads);

    public static AbstractHandler getHandler(String topic){
        String type = getTypeFromTopic(topic);
        return handlerMap.get(type);
    }

    protected Integer getDeviceIdFromTopic(String topic){
        int idx = topic.lastIndexOf("/");
        return Integer.valueOf(topic.substring(idx+1));
    }
    private static String getTypeFromTopic(String topic){
        int idx = topic.lastIndexOf("/");
        String dataStr = topic.substring(0,idx);
        idx = dataStr.lastIndexOf("/");
        return dataStr.substring(idx+1);
    }

}
