package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.device.DevicePo;
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
        for (Integer childDeviceId : childIds) {
            DevicePo devicePo = deviceMapper.selectById(childDeviceId);
            String childId = devicePo.getChildId();
            childIdList.add(childId);
            Integer modelId = devicePo.getModelId();
            Integer typeId = deviceModelMapper.selectById(modelId).getTypeId();
            if(mMap.containsKey(typeId)){
                String mName = mMap.get(typeId);
                mbMap.put(childId, mName);
                continue;
            }
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
            String stopWatch = deviceTypePo.getStopWatch();
            String mName = "m" + flag;
            mMap.put(typeId, mName);
            mbMap.put(mName, JSONObject.parseObject(stopWatch));
            flag++;
            mbMap.put(childId, mName);
        }
        mbMap.put("n", childIdList);
        mb.put("mb", mbMap);
        String sendTopic = "/down2/stopWatch/" + deviceId;
        mqttService.sendMessage(sendTopic,  mb.toString());
    }
}
