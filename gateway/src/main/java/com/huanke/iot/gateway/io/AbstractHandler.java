package com.huanke.iot.gateway.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.huanke.iot.gateway.data.DeviceDataBase;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:17
 **/
public abstract  class AbstractHandler<? extends  DeviceDataBase> {

    protected Integer getDeviceIdFromTopic(String topic){
        int idx = topic.lastIndexOf("/");
        return Integer.valueOf(topic.substring(idx));
    }
    public void handlerData(String device,String message){
        D data = JSON.parseObject(message,D.class);

    }
    public abstract void  doHandler(String topic, D data);
}
