package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.data.DeviceControlMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.data.DeviceControlData;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.powercheck.PowerCheckService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ControlHandler extends AbstractHandler {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceControlMapper deviceControlMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private PowerCheckService powerCheckService;
    @Data
    public static class FuncItemMessage{
        private String type;
        private Integer value;
        private String childid;
    }

    @Data
    public static class FuncListMessage{
        private Integer msg_id;
        private String msg_type;
        private List<ControlHandler.FuncItemMessage> datas;
    }

    protected String getTopicType() {
        return "control";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {

        String message = new String(payloads);
        ControlHandler.FuncListMessage funcListMessage = JSON.parseObject(new String(payloads),ControlHandler.FuncListMessage.class);

        funcListMessage.getDatas().forEach(funcItemMessage -> {
            Integer deviceId = getDeviceIdFromTopic(topic);
            if(funcItemMessage.getChildid() != null){
                DevicePo childDevice = deviceMapper.getChildDevice(deviceId, funcItemMessage.getChildid());
                deviceId = childDevice.getId();
            }
            DeviceControlData deviceControlData = new DeviceControlData();
            deviceControlData.setFuncId(funcItemMessage.getType());
            deviceControlData.setFuncValue(funcItemMessage.getValue());
            deviceControlData.setCreateTime(System.currentTimeMillis());
            deviceControlData.setDeviceId(deviceId);
            try {
                //上报指令为210且值为1,重置开机状态
                if(deviceControlData.getFuncId().equals("210") && deviceControlData.getFuncValue().equals(1)){
                    this.powerCheckService.resetPowerStatus(deviceControlData.getDeviceId());
                }
                deviceControlMapper.insert(deviceControlData);
                //如果上报状态为开关机状态则进行记录
                stringRedisTemplate.opsForHash().put("control2." + deviceId, funcItemMessage.getType(), String.valueOf(funcItemMessage.getValue()));
            }catch (Exception e){
                log.error("",e);
            }
        });


    }
}
