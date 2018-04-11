package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.po.device.data.DeviceControlData;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ControlHandler extends AbstractHandler {

    @Data
    private static class SwitchDataItem{
        private Integer mode;
        private Integer devicelock;
        private Integer childlock;
        private Integer anion;
        private Integer uvl;
        private Integer heater;
        private List<Item> fan;
        private List<Item>  valve;
    }
    @Data
    private static class Item{
        private Integer index;
        private Integer value;
    }

    @Override
    protected String getTopicType() {
        return "control";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        String message = new String(payloads);
        SwitchDataItem switchDataItem = JSON.parseObject(message,SwitchDataItem.class);
        DeviceControlData deviceControlData = new DeviceControlData();
        deviceControlData.setDeviceId(getDeviceIdFromTopic(topic));
        deviceControlData.setChildlock(switchDataItem.getChildlock());
        deviceControlData.setAnion(switchDataItem.getAnion());
        if(switchDataItem.getFan() != null) {
            deviceControlData.setFan(JSON.toJSONString(switchDataItem.getFan()));
        }
        if(switchDataItem.getValve() != null) {
            deviceControlData.setValve(JSON.toJSONString(switchDataItem.getValve()));
        }
        deviceControlData.setMode(switchDataItem.getMode());
        deviceControlData.setHeater(switchDataItem.getHeater());
        deviceControlData.setDevicelock(switchDataItem.getDevicelock());
        deviceControlData.setUvl(switchDataItem.getUvl());
        deviceControlData.setCreateTime(System.currentTimeMillis());

    }
}
