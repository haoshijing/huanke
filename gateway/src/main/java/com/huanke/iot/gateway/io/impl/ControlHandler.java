package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.data.DeviceControlMapper;
import com.huanke.iot.base.dao.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.data.DeviceControlData;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.powercheck.PowerCheckService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@Slf4j
public class ControlHandler extends AbstractHandler {

    private static final List<String> sensor_datas = Arrays.asList("111", "120", "130", "131", "140", "141", "150", "160", "110", "170", "180", "142", "143");
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceControlMapper deviceControlMapper;
    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;
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

    @Override
    protected String getTopicType() {
        return "control";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {

        String datas = new String(payloads);
        ControlHandler.FuncListMessage funcListMessage = JSON.parseObject(datas,ControlHandler.FuncListMessage.class);
        funcListMessage.getDatas().forEach(funcItemMessage -> {
            Integer deviceId = getDeviceIdFromTopic(topic);
            String childId = funcItemMessage.getChildid();
            if (!StringUtils.equals("0",childId)){
                DevicePo childDevice = deviceMapper.getChildDevice(deviceId, childId);
                if(childDevice != null){
                    deviceId = childDevice.getId();
                }else{
                    return;
                }
            }
            DeviceControlData deviceControlData = new DeviceControlData();
            deviceControlData.setFuncId(funcItemMessage.getType());
            deviceControlData.setFuncValue(funcItemMessage.getValue());
            deviceControlData.setCreateTime(System.currentTimeMillis());
            deviceControlData.setDeviceId(deviceId);
            try {
                //上报指令为210且值不为0,重置为开机状态
                if(deviceControlData.getFuncId().equals("210")){
                    if(!deviceControlData.getFuncValue().equals(0)) {
                        this.powerCheckService.resetPowerStatus(deviceControlData.getDeviceId());
                    }else{
                        //log.info("deviceId = {}, send close machine ", deviceControlData.getDeviceId());
                        DevicePo updatePo = new DevicePo();
                        updatePo.setId(deviceId);
                        updatePo.setPowerStatus(DeviceConstant.POWER_STATUS_YES);
                        deviceMapper.updateById(updatePo);
                    }
                }
                if(sensor_datas.contains(deviceControlData.getFuncId())){
                    //如果是传感器数据，存到sensor_data一份
                    DeviceSensorPo deviceSensorPo = new DeviceSensorPo();

                    deviceSensorPo.setSensorType(funcItemMessage.getType());
                    Integer value = funcItemMessage.getValue();
                    if(value == null){
                        value = 0;
                    }
                    deviceSensorPo.setSensorValue(value);
                    deviceSensorPo.setCreateTime(System.currentTimeMillis());
                    deviceSensorPo.setDeviceId(deviceId);
                    deviceSensorDataMapper.insert(deviceSensorPo);
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
