package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.api.controller.h5.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

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
    private StringRedisTemplate stringRedisTemplate;

    public DeviceDetailVo queryDetailByDeviceId(String deviceId) {
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        //指令类别
        if(devicePo!=null){
         Integer deviceTypeId =   devicePo.getDeviceTypeId();
        }
        return  null;
    }

    public String sendFunc(DeviceFuncVo deviceFuncVo) {
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceFuncVo.getDeviceId());
        if(devicePo != null){
            Integer deviceId = devicePo.getId();
            String topic = "/down/control/"+deviceId;
            String requestId = UUID.randomUUID().toString().replace("-","");
            DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
            deviceOperLogPo.setFuncId(deviceFuncVo.getFuncId());
            deviceOperLogPo.setFuncValue(deviceFuncVo.getValue());
            deviceOperLogPo.setRequestId(requestId);
            deviceOperLogPo.setCreateTime(System.currentTimeMillis());
            deviceOperLogMapper.insert(deviceOperLogPo);

            mqttSendService.sendMessage(topic,"");
            return requestId;
        }
        return "";
    }
}
