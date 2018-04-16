package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.po.device.data.DeviceAlarmPo;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class SensorHandler  extends AbstractHandler {

    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Data
    public static class SensorMessage{
        private Integer type;
        private String value;
    }

    @Data
    public static class SensorListMessage{
        private Integer msg_id;
        private String msg_type;
        private List<SensorHandler.SensorMessage> sensor;
    }

    @Override
    protected String getTopicType() {
        return "sensor";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        SensorHandler.SensorListMessage sensorListMessage = JSON.parseObject(new String(payloads),SensorHandler.SensorListMessage.class);

        sensorListMessage.getSensor().forEach(sensorMessage -> {
            Integer deviceId = getDeviceIdFromTopic(topic);
            DeviceSensorPo deviceSensorPo = new DeviceSensorPo();
            deviceSensorPo.setSensorType(sensorMessage.getType());
            deviceSensorPo.setSensorValue(sensorMessage.getValue());
            deviceSensorPo.setCreateTime(System.currentTimeMillis());
            deviceSensorDataMapper.insert(deviceSensorPo);
            stringRedisTemplate.opsForHash().put("sensor."+deviceId,sensorMessage.getType(),sensorMessage.getValue());
        });
    }

    public static void main(String[] args) {

    }
}
