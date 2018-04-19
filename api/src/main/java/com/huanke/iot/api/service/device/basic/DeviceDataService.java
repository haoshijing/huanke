package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSONArray;
import com.huanke.iot.api.controller.h5.req.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.DeviceTypeMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.DeviceTypePo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class DeviceDataService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private MqttSendService mqttSendService;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public DeviceDetailVo queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = new DeviceDetailVo();
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        //指令类别
        if (devicePo != null) {
            Integer deviceTypeId = devicePo.getDeviceTypeId();
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(deviceTypeId);
            if (deviceTypePo != null) {
                String funcTypeListStr = deviceTypePo.getFuncList();
                String[] funcTypes = funcTypeListStr.split(",");
                List<JSONArray> funcDatas = Lists.newArrayList();
                List<JSONArray> sensorDatas = Lists.newArrayList();
                deviceDetailVo.setDeviceFuncData(funcDatas);
                deviceDetailVo.setDeviceSenorData(sensorDatas);
                for (String funcType : funcTypes) {
                    JSONArray jsonArray = new JSONArray();
                    String data = (String) stringRedisTemplate.opsForHash().get("control." + devicePo.getId(), funcType);
                    DeviceDetailVo.DeviceFuncVo deviceFuncVo = new DeviceDetailVo.DeviceFuncVo();
                    deviceFuncVo.setCurrentValue(data);

                    FuncTypeEnums funcTypeEnums = FuncTypeEnums.getByCode(funcType);
                    if(funcTypeEnums != null) {
                        deviceFuncVo.setFuncName(funcTypeEnums.getMark());
                        deviceFuncVo.setFuncId(funcTypeEnums.getCode());
                        deviceFuncVo.setFuncType(funcTypeEnums.getFuncType());
                        deviceFuncVo.setRange(funcTypeEnums.getRange());
                        jsonArray.add(deviceFuncVo);
                    }
                    funcDatas.add(jsonArray);
                }

                String sensorListStr = deviceTypePo.getSensorList();
                String[] sensorTypes = sensorListStr.split(",");
                for(String sensorType:sensorTypes){
                    JSONArray jsonArray = new JSONArray();
                    String data = (String)stringRedisTemplate.opsForHash().get("sensor." + devicePo.getId(), sensorType);
                    SensorTypeEnums sensorTypeEnums = SensorTypeEnums.getByCode(sensorType);
                    DeviceDetailVo.DeviceSensorVo deviceSensorVo = new DeviceDetailVo.DeviceSensorVo();
                    deviceSensorVo.setSenorName(sensorTypeEnums.getMark());
                    deviceSensorVo.setUnit(sensorTypeEnums.getUnit());
                    deviceSensorVo.setSensorType(sensorType);
                    deviceSensorVo.setSensorValue(data);
                    jsonArray.add(deviceSensorVo);
                }
            }

        }
        return deviceDetailVo;
    }

    public String sendFunc(DeviceFuncVo deviceFuncVo) {
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceFuncVo.getDeviceId());
        if (devicePo != null) {
            Integer deviceId = devicePo.getId();
            String topic = "/down/control/" + deviceId;
            String requestId = UUID.randomUUID().toString().replace("-", "");
            DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
            deviceOperLogPo.setFuncId(deviceFuncVo.getFuncId());
            deviceOperLogPo.setFuncValue(deviceFuncVo.getValue());
            deviceOperLogPo.setRequestId(requestId);
            deviceOperLogPo.setCreateTime(System.currentTimeMillis());
            deviceOperLogMapper.insert(deviceOperLogPo);

            mqttSendService.sendMessage(topic, "");
            return requestId;
        }
        return "";
    }
}
