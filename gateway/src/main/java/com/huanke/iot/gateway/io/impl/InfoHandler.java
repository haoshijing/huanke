package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class InfoHandler extends AbstractHandler {

    @Autowired
    private DeviceMapper deviceMapper;

    @Data
    private static class InfoItem {
        private Info info;

    }

    @Data
    private static class Info{
        WxInfoItem wx_info;
        private VersionItem version;
        private String imei;
        private String imsi;
        private String mac;
    }

    @Data
    private static class WxInfoItem {
        private String dev_id;
        private String dev_license;
    }

    @Data
    private static class VersionItem {
        private String software;
        private String hardware;
    }

    @Override
    protected String getTopicType() {
        return "info";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        String message = new String(payloads);
        InfoItem infoItem = JSON.parseObject(message, InfoItem.class);
        if (infoItem != null) {
            String devId = "";
            if (infoItem.getInfo() != null &&
                    StringUtils.isNotEmpty(infoItem.getInfo().getWx_info().getDev_id())) {
                devId = infoItem.getInfo().getWx_info().getDev_id();
            }
            if (StringUtils.isEmpty(devId)) {
                log.warn("devId {} is blank ", devId);
                return;
            }
            try {
                DevicePo devicePo = deviceMapper.selectByWxDeviceId(devId);
                if (devicePo == null) {
                    log.warn("devId {} :is not belong a exist device ", devId);
                    return;
                } else {
                    devicePo.setImei(infoItem.getInfo().getImei());
                    devicePo.setImsi(infoItem.getInfo().getImsi());
                    devicePo.setMac(infoItem.getInfo().getMac());
                    devicePo.setLastUpdateTime(System.currentTimeMillis());
                    devicePo.setVersion(JSON.toJSONString(infoItem.getInfo().getVersion()));
                    deviceMapper.updateById(devicePo);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
}
