package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.service.DeviceParamService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述:
 * 设备传参Handler
 *
 * @author onlymark
 * @create 2018-10-30 下午3:29
 */
@Repository
@Slf4j
public class CfgHandler extends AbstractHandler {
    @Autowired
    private DeviceParamService deviceParamService;

    @Data
    public static class CfgMessage {
        private List<CfgConfig> datas;
    }

    @Data
    public static class CfgConfig {
        private String type;
        private List<String> value;
    }

    @Override
    protected String getTopicType() {
        return "cfg";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        Integer deviceId = getDeviceIdFromTopic(topic);
        try {
            CfgMessage cfgMessage = JSON.parseObject(new String(payloads), CfgMessage.class);
            List<CfgConfig> cfgConfigs = cfgMessage.getDatas();
            deviceParamService.updateParam(deviceId, cfgConfigs);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
