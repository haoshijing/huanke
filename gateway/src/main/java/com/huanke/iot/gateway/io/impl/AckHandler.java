package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author haoshijing
 * @version 2018年04月17日 13:15
 **/
@Repository
public class AckHandler extends AbstractHandler {

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;
    @Data
    private class AckMessage{
        private String requestId;
        private Integer ret;
        private String retMsg;
    }
    @Override
    protected String getTopicType() {
        return "ack";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        AckMessage ackMessage = JSON.parseObject(new String(payloads),AckMessage.class);
        if(ackMessage != null){
            DeviceOperLogPo updatePo = new DeviceOperLogPo();
            updatePo.setResponseTime(System.currentTimeMillis());
            updatePo.setDealRet(ackMessage.getRet());
            updatePo.setRequestId(ackMessage.getRequestId());
            deviceOperLogMapper.updateByRequestId(updatePo);
        }
    }
}
