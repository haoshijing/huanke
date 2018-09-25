package com.huanke.iot.gateway.io.impl;

import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.mqttlistener.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-23 上午11:23
 */
public class StopWatchHandler extends AbstractHandler {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
        Integer hostModelId = deviceMapper.selectById(deviceId).getModelId();
        Integer hostDeviceTypeId = deviceModelMapper.selectById(hostModelId).getTypeId();
        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(hostDeviceTypeId);
        String stopWatch = deviceTypePo.getStopWatch();
        String sendTopic = "/down/stopWatch/" + deviceId;
        mqttService.sendMessage(sendTopic, stopWatch);
    }
}
