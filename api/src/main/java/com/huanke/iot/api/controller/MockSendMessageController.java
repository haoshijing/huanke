package com.huanke.iot.api.controller;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.gateway.MqttSendService;
import lombok.Data;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

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
    public static class SwitchData{
        SwitchDataItem key;
    }

    @Data
    public static class AlarmListMessage{
        private List<AlarmMessage> alarm;
    }

    @Data
    private static class SwitchDataItem{
        private String requestId = UUID.randomUUID().toString().replace("-","");
        private Integer mode;
        private Integer devicelock;
        private Integer childlock;
        private Integer anion;
        private Integer uvl;
        private Integer heater;
        private List<Item> fan;
        private List<Item>  valve;
    }
    @Data
    private static class Item{
        private Integer index;
        private Integer value;
    }

    @Autowired
    private MqttSendService mqttSendService;

    @RequestMapping("/sendData")
    @ResponseBody
    public String sendData(@RequestBody SwitchData switchData ){
        switchData.getKey().setRequestId(UUID.randomUUID().toString().replace("-",""));
        String topic = "/down/control/1";
        mqttSendService.sendMessage(topic,JSON.toJSONString(switchData),true);
        return "ok";
    }

    @RequestMapping("/sendControl")
    @ResponseBody
    public String sendControl(){
        SwitchData switchDataItem = new SwitchData();
        SwitchDataItem item = new SwitchDataItem();
        item.setMode(1);
        item.setAnion(1);

        Item item1 = new Item();
        item1.setIndex(0);
        item1.setValue(2);
        Item item12 = new Item();
        item12.setIndex(1);
        item12.setValue(3);

        item.setFan(Lists.newArrayList(item1,item12));
        item.setHeater(1);
        switchDataItem.setKey(item);
        String topic = "/down/control/1";
        mqttSendService.sendMessage(topic,JSON.toJSONString(switchDataItem),true);
        return "ok";
    }


    @ResponseBody
    @RequestMapping("/sendAlarm")
    public String sendAlarmMessage(){
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.setIndex(1);
        alarmMessage.setType(1);
        alarmMessage.setValue(2);
        AlarmListMessage alarmListMessage = new AlarmListMessage();
        alarmListMessage.setAlarm(Lists.newArrayList(alarmMessage));
        String message = JSON.toJSONString(alarmListMessage);
        mqttSendService.sendMessage("/up/alarm/1",message,true);
        return "111";
    }
}
