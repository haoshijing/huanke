package com.huanke.iot.api.controller;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.gateway.MqttSendService;
import lombok.Data;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/sendmessage")
public class MockSendMessageController {
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

    @Autowired
    private MqttSendService mqttSendService;

    @RequestMapping("/sendAlarm")
    public String sendAlarmMessage(){
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.setIndex(1);
        alarmMessage.setType(1);
        alarmMessage.setValue(2);
        AlarmListMessage alarmListMessage = new AlarmListMessage();
        alarmListMessage.setAlarm(Lists.newArrayList(alarmMessage));
        String message = JSON.toJSONString(alarmListMessage);
        mqttSendService.sendMessage("/up/alarm/1",message);
        return "111";
    }
}
