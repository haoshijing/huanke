package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
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
import lombok.Data;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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

    @Data
    public static class FuncItemMessage {
        private String type;
        private String value;
    }

    @Data
    public static class FuncListMessage {
        private String msg_id;
        private String msg_type;
        private List<DeviceDataService.FuncItemMessage> datas;
    }

    public DeviceDetailVo queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = new DeviceDetailVo();
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if(devicePo != null){
            getIndexData(deviceDetailVo,devicePo.getId());
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
            deviceOperLogPo.setDeviceId(deviceId);
            deviceOperLogPo.setFuncValue(deviceFuncVo.getValue());
            deviceOperLogPo.setRequestId(requestId);
            deviceOperLogPo.setCreateTime(System.currentTimeMillis());
            deviceOperLogMapper.insert(deviceOperLogPo);
            FuncListMessage funcListMessage = new FuncListMessage();
            funcListMessage.setMsg_type("control");
            funcListMessage.setMsg_id(requestId);
            FuncItemMessage funcItemMessage = new FuncItemMessage();
            funcItemMessage.setType(deviceFuncVo.getFuncId());
            funcItemMessage.setValue(deviceFuncVo.getValue());
            funcListMessage.setDatas(Lists.newArrayList(funcItemMessage));
            mqttSendService.sendMessage(topic, JSON.toJSONString(funcListMessage));
            return requestId;
        }
        return "";
    }

    private void getIndexData(DeviceDetailVo deviceDetailVo, Integer deviceId) {

        Map<Object, Object> datas = stringRedisTemplate.opsForHash().entries("sensor." + deviceId);

        Map<Object, Object> controlDatas = stringRedisTemplate.opsForHash().entries("control." + deviceId);

        DeviceDetailVo.SysDataItem pm = new DeviceDetailVo.SysDataItem();
        pm.setData(getData(datas,SensorTypeEnums.PM25_IN.getCode()));
        pm.setUnit(SensorTypeEnums.PM25_IN.getUnit());
        deviceDetailVo.setPm(pm);

        DeviceDetailVo.SysDataItem co2 = new DeviceDetailVo.SysDataItem();
        pm.setData(getData(datas,SensorTypeEnums.CO2_IN.getCode()));
        pm.setUnit(SensorTypeEnums.CO2_IN.getUnit());
        deviceDetailVo.setCo2(co2);



        DeviceDetailVo.SysDataItem tvoc = new DeviceDetailVo.SysDataItem();
        tvoc.setData(getData(datas,SensorTypeEnums.TVOC_IN.getCode()));
        tvoc.setUnit(SensorTypeEnums.TVOC_IN.getUnit());
        deviceDetailVo.setTvoc(tvoc);


        DeviceDetailVo.SysDataItem hcho = new DeviceDetailVo.SysDataItem();
        hcho.setData(getData(datas,SensorTypeEnums.HCHO_IN.getCode()));
        hcho.setUnit(SensorTypeEnums.HCHO_IN.getUnit());
        deviceDetailVo.setHcho(hcho);



        DeviceDetailVo.SysDataItem tem = new DeviceDetailVo.SysDataItem();
        tem.setData(getData(datas,SensorTypeEnums.TEMPERATURE_IN.getCode()));
        tem.setUnit(SensorTypeEnums.TEMPERATURE_IN.getUnit());
        deviceDetailVo.setTem(tem);



        DeviceDetailVo.SysDataItem hum = new DeviceDetailVo.SysDataItem();
        hum.setData(getData(datas,SensorTypeEnums.HUMIDITY_IN.getCode()));
        hum.setUnit(SensorTypeEnums.HUMIDITY_IN.getUnit());
        deviceDetailVo.setHum(hum);

        DeviceDetailVo.SysDataItem screen = new DeviceDetailVo.SysDataItem();
        screen.setData(getData(controlDatas,FuncTypeEnums.TIMER_SCREEN.getCode()));
        screen.setUnit("秒");
        deviceDetailVo.setScreen(screen);


        DeviceDetailVo.SysDataItem remain = new DeviceDetailVo.SysDataItem();
        remain.setData(getData(controlDatas,FuncTypeEnums.TIMER_REMAIN.getCode()));
        remain.setUnit("秒");
        deviceDetailVo.setHum(hum);

    }

    private String getData(Map<Object,Object> map,String key){
        if(map.containsKey(key)){
            return (String) map.get(key);
        }
        return "";
    }
}
