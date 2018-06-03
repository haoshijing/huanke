package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.data.DeviceAlarmMapper;
import com.huanke.iot.base.po.device.data.DeviceAlarmPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public  class AlarmHandler extends AbstractHandler {

    @Autowired
    private DeviceAlarmMapper deviceAlarmMapper;
    @Data
    public static class AlarmMessage{
        private Integer index;
        private Integer type;
        private Integer value;
    }

    @Data
    public static class AlarmListMessage{
        private List<AlarmMessage> alarm;
    }

    @Override
    protected String getTopicType() {
        return "alarm";
    }

    @Override
    public void doHandler(String topic, byte[]  datas) {
        AlarmListMessage alarmListMesage = JSON.parseObject(new String(datas),AlarmListMessage.class);
        if(alarmListMesage != null){
            List<AlarmMessage> alarmMessages = alarmListMesage.getAlarm();
             List<DeviceAlarmPo> deviceAlarmPos =  alarmMessages.stream().map(alarmMessage -> {
               DeviceAlarmPo deviceAlarmPo = new DeviceAlarmPo();
               BeanUtils.copyProperties(alarmMessage,deviceAlarmPo);
               deviceAlarmPo.setDeviceId(getDeviceIdFromTopic(topic));
               return deviceAlarmPo;
            }).collect(Collectors.toList());
            deviceAlarmPos.forEach(deviceAlarmPo -> {
                deviceAlarmPo.setCreateTime(System.currentTimeMillis());
                //deviceAlarmMapper.insert(deviceAlarmPo);
            });
        }
    }
}
