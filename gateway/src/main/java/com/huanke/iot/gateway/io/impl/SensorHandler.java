package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.mqttlistener.MqttService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SensorHandler  extends AbstractHandler {

    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private MqttService mqttService;

    private Map<String,Integer> maxValue;

    private Map<String,Integer> minValue;
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
        if(maxValue==null || maxValue.keySet()==null||maxValue.keySet().size()<=0){
            maxValue = new HashMap<String,Integer>();
            maxValue.put("150",deviceAbilityMapper.selectByDirValue("150").getMaxVal());//TVOC上限
            maxValue.put("160",deviceAbilityMapper.selectByDirValue("160").getMaxVal());//甲醛上限
        }
        SensorHandler.SensorListMessage sensorListMessage = JSON.parseObject(new String(payloads),SensorHandler.SensorListMessage.class);

        Integer deviceIdPower = getDeviceIdFromTopic(topic);
        if(deviceIdPower == 444){
            //能源管理后台测试
            String topicPower = "/down2/powerManage/" + deviceIdPower;
            mqttService.sendMessage(topicPower, JSON.toJSONString(sensorListMessage),true);
        }

        List<SensorHandler.SensorMessage> datasPower = new ArrayList<>();
        sensorListMessage.getDatas().forEach(sensorMessage -> {
            Integer deviceId = getDeviceIdFromTopic(topic);
            if(sensorMessage.getChildid() != null && !sensorMessage.getChildid().equals("0")){
                DevicePo childDevice = deviceMapper.getChildDevice(deviceId, sensorMessage.getChildid());
                if(childDevice != null){
                    deviceId = childDevice.getId();
                }else{
                    return;
                }
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


            deviceSensorPo.setDeviceId(deviceId);
            try {
                //添加最大值限制，目前为甲醛和TVOC
                if(maxValue.get(type)==null||value.compareTo(maxValue.get(type))<=0) {
                    deviceSensorDataMapper.insert(deviceSensorPo);
                    stringRedisTemplate.opsForHash().put("sensor2." + deviceId, type, String.valueOf(value));
                }
            }catch (Exception e){
                log.error("",e);
            }
        });
    }

    public static void main(String[] args) {

    }
}
