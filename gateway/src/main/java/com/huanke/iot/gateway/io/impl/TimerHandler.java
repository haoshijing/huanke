package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.data.DeviceTimerMapper;
import com.huanke.iot.base.po.device.data.DeviceTimerPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TimerHandler extends AbstractHandler {

    @Autowired
    private DeviceTimerMapper deviceTimerMapper;
    @Data
    public static class  TimerItem{
        private Integer timing_on;
        private Integer timing_off;
        private List<TimerHandler.Item> hepa;
        private List<TimerHandler.Item> acticarbon;
    }

    @Data
    public static class Item{
        private Integer index;
        private Integer value;
    }

    @Override
    protected String getTopicType() {
        return "timer";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        String message = new String(payloads);
        TimerItem timerItem = JSON.parseObject(message,TimerItem.class);

        DeviceTimerPo deviceTimerPo = new DeviceTimerPo();
        deviceTimerPo.setActicarbon(JSON.toJSONString(timerItem.getActicarbon()));
        deviceTimerPo.setTimingOn(timerItem.getTiming_on());
        deviceTimerPo.setTimingOff(timerItem.getTiming_off());
        deviceTimerPo.setHepa(JSON.toJSONString(timerItem.getHepa()));

        deviceTimerMapper.insert(deviceTimerPo);
    }
}
