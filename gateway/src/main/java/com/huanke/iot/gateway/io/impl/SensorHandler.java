package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.data.DeviceAlarmMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorMapper;
import com.huanke.iot.base.po.device.data.DeviceAlarmPo;
import com.huanke.iot.base.po.device.data.DeviceSensorDataPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SensorHandler  extends AbstractHandler {

    @Autowired
    private DeviceSensorMapper deviceSensorMapper;
    @Data
    public static class SensorMessage{
        private Integer index;
        private Integer pm2_5;
        private Integer co2;
        /**
         * 湿度
         */
        private Integer humidity;
        /**
         * 温度
         */
        private Integer temperature;
        /**
         * 甲醛值
         */
        private Integer tyoc;
        /**
         * 甲醛化学因子数
         */
        private Integer hcho;
    }

    @Data
    public static class SensorListMessage{
        private List<SensorHandler.SensorMessage> sensor;
    }

    @Override
    protected String getTopicType() {
        return "sensor";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        SensorHandler.SensorListMessage sensorListMessage = JSON.parseObject(new String(payloads),SensorHandler.SensorListMessage.class);
        if(sensorListMessage != null){
            List<SensorHandler.SensorMessage> sensorMessages = sensorListMessage.getSensor();
            List<DeviceSensorDataPo> sensorDataPos =  sensorMessages.stream().map(sensorMessage -> {
                DeviceSensorDataPo deviceSensorDataPo = new DeviceSensorDataPo();
                BeanUtils.copyProperties(deviceSensorDataPo,sensorMessage);
                deviceSensorDataPo.setDeviceId(getDeviceIdFromTopic(topic));
                return deviceSensorDataPo;
            }).collect(Collectors.toList());
            sensorDataPos.forEach(senorPo-> {
                senorPo.setCreateTime(System.currentTimeMillis());
                deviceSensorMapper.insert(senorPo);
            });
        }
    }
}
