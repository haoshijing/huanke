package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.mqttlistener.MqttService;
import lombok.Data;
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
    private DeviceMapper deviceMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MqttService mqttService;

    @Data
    public static class StopWatchMessage{
        private Integer hostDeviceId;
        private String address;
    }

    @Override
    protected String getTopicType() {
        return "stopWatch";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        StopWatchHandler.StopWatchMessage stopWatchMessage = JSON.parseObject(new String(payloads),StopWatchHandler.StopWatchMessage.class);
        Integer hostDeviceId = stopWatchMessage.getHostDeviceId();
        String address = stopWatchMessage.getAddress();
        DevicePo devicePo = deviceMapper.getChildDevice(hostDeviceId, address);
        Integer devicePoId = devicePo.getId();
        String childDeviceStopWatch = stringRedisTemplate.opsForValue().get("childStopWatch." + devicePoId);
        String sendTopic = "/down/control/" + hostDeviceId + address;
        mqttService.sendMessage(sendTopic, childDeviceStopWatch);
    }
}
