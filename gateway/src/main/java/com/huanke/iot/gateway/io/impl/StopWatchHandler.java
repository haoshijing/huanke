package com.huanke.iot.gateway.io.impl;

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

    @Override
    protected String getTopicType() {
        return "stopWatch";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        Integer deviceId = getDeviceIdFromTopic(topic);
        String childDeviceStopWatch = stringRedisTemplate.opsForValue().get("childStopWatch." + deviceId);
        String sendTopic = "/down/stopWatch/" + deviceId;
        mqttService.sendMessage(sendTopic, childDeviceStopWatch);
    }
}
