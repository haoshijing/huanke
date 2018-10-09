package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class SensorHandler  extends AbstractHandler {

    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceMapper deviceMapper;
    @Data
    public static class SensorMessage{
        private String type;
        private Integer value;
        private String childid;
        //根据硬件要求，重新设定type，value
        private String t;
        private Integer v;
    }

    @Data
    public static class SensorListMessage{
        private Integer msg_id;
        private String msg_type;
        private List<SensorHandler.SensorMessage> datas;
    }

    @Override
    protected String getTopicType() {
        return "sensor";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        SensorHandler.SensorListMessage sensorListMessage = JSON.parseObject(new String(payloads),SensorHandler.SensorListMessage.class);

        sensorListMessage.getDatas().forEach(sensorMessage -> {
            Integer deviceId = getDeviceIdFromTopic(topic);
            if(sensorMessage.getChildid() != null){
                DevicePo childDevice = deviceMapper.getChildDevice(deviceId, sensorMessage.getChildid());
                deviceId = childDevice.getId();
            }
            DeviceSensorPo deviceSensorPo = new DeviceSensorPo();
            String type = sensorMessage.getType();
            if(StringUtils.isBlank(type)){
                type = sensorMessage.getT();
            }
            deviceSensorPo.setSensorType(type);
            Integer value = sensorMessage.getValue();
            if(value == null){
                value = sensorMessage.getV();
            }
            deviceSensorPo.setSensorValue(value);
            deviceSensorPo.setCreateTime(System.currentTimeMillis());


            deviceSensorPo.setDeviceId(getDeviceIdFromTopic(topic));
            try {
                deviceSensorDataMapper.insert(deviceSensorPo);
                stringRedisTemplate.opsForHash().put("sensor." + deviceId, type, String.valueOf(value));
            }catch (Exception e){
                log.error("",e);
            }
        });
    }

    public static void main(String[] args) {

    }
}
