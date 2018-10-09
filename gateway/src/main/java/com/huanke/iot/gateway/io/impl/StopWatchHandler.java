package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.mqttlistener.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-23 上午11:23
 */
@Repository
@Slf4j
public class StopWatchHandler extends AbstractHandler {

    @Autowired
    private MqttService mqttService;
    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    protected String getTopicType() {
        return "stopWatch";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        Integer deviceId = getDeviceIdFromTopic(topic);
        List<Integer> childIds = deviceMapper.queryChildDeviceIds(deviceId);
        //拼接码表
        JSONObject mb = new JSONObject();
        Map<String, Object> mbMap = new HashMap<>();
        List<String> childIdList = new ArrayList<>();
        int flag = 1;
        Map<Integer, String> mMap = new HashMap<>();
        for (Integer childId : childIds) {
            childIdList.add(String.valueOf(childId));
            Integer modelId = deviceMapper.selectById(deviceId).getModelId();
            if(mMap.containsKey(modelId)){
                String mValue = mMap.get(modelId);
                mbMap.put(String.valueOf(childId), mValue);
                continue;
            }
            Integer typeId = deviceModelMapper.selectById(modelId).getTypeId();
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
            String stopWatch = deviceTypePo.getStopWatch();
            String mName = "m" + flag;
            mMap.put(modelId, mName);
            mbMap.put(mName, JSONObject.parseObject(stopWatch));
            flag++;
            mbMap.put(String.valueOf(childId), mName);
        }
        mbMap.put("n", childIdList);
        String sendTopic = "/down/stopWatch/" + deviceId;
        mqttService.sendMessage(sendTopic,  mb.toString());
    }
}
