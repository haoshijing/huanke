package com.huanke.iot.job;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.DeviceTimerMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.DeviceTimerPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.job.gateway.MqttSendService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
public class DeviceTimerJob {

    @Autowired
    private MqttSendService mqttSendService;
    @Autowired
    private DeviceTimerMapper deviceTimerMapper;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Scheduled(cron = "0/15 * * * * ?")
    public void doWork(){

        Long t = System.currentTimeMillis();
        List<DeviceTimerPo> deviceTimerPos = deviceTimerMapper.queryTimers(t);
        deviceTimerPos.forEach(deviceTimerPo -> {
            Integer deviceId = deviceTimerPo.getDeviceId();
            if(deviceTimerPo.getTimerType() == 1){
                sendFunc(deviceId, FuncTypeEnums.TIMER_OEPN.getCode());
            }else{
                sendFunc(deviceId, FuncTypeEnums.TIMER_CLOSE.getCode());
            }
            DeviceTimerPo updatePo = new DeviceTimerPo();
            updatePo.setId(deviceTimerPo.getId());
            updatePo.setStatus(4);
            updatePo.setExecuteRet(2);
            updatePo.setExecuteTime(System.currentTimeMillis());
            deviceTimerMapper.updateById(updatePo);
        });
    }

    public String sendFunc(Integer deviceId,String funcId) {
            String topic = "/down/control/" + deviceId;
            String requestId = UUID.randomUUID().toString().replace("-", "");
            DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
            deviceOperLogPo.setFuncId(funcId);
            deviceOperLogPo.setDeviceId(deviceId);
            deviceOperLogPo.setRequestId(requestId);
            deviceOperLogPo.setCreateTime(System.currentTimeMillis());
            deviceOperLogMapper.insert(deviceOperLogPo);
            FuncListMessage funcListMessage = new FuncListMessage();
            funcListMessage.setMsg_type("control");
            funcListMessage.setMsg_id(requestId);
            FuncItemMessage funcItemMessage = new FuncItemMessage();
            funcItemMessage.setType(funcId);
            funcListMessage.setDatas(Lists.newArrayList(funcItemMessage));
            mqttSendService.sendMessage(topic, JSON.toJSONString(funcListMessage));
            return requestId;

    }

@Data
public static class FuncItemMessage {
    private String type;
    private String value;
}

@Data
public static class FuncListMessage {
    private String msg_id;
    private String msg_type;
    private List<FuncItemMessage> datas;
}

}
