package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.data.DeviceInfoMapper;
import com.huanke.iot.base.po.device.data.DeviceInfoPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InfoHandler extends AbstractHandler {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    @Data
    private static class InfoItem{
        private String imei;
        private String imsi;
        private String mac;
        private VersionItem version;
        private WxInfoItem wx_info;

    }
    @Data
    private static class WxInfoItem{
        private String wx_dev_type;
        private String wx_dev_id;
        private String wx_device_licence;
    }

    @Data
    private static class VersionItem{
        private String software;
        private String hardward;
    }
    @Override
    protected String getTopicType() {
        return "location";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        String message = new String(payloads);
        InfoItem infoItem = JSON.parseObject(message,InfoItem.class);
        if(infoItem != null){
            DeviceInfoPo deviceInfoPo = new DeviceInfoPo();
            deviceInfoPo.setCreateTime(System.currentTimeMillis());
            deviceInfoPo.setImei(infoItem.getImei());
            deviceInfoPo.setImsi(infoItem.getImsi());
            deviceInfoPo.setMac(infoItem.getMac());
            deviceInfoPo.setVersion(JSON.toJSONString(infoItem.getVersion()));
            deviceInfoPo.setWxInfo(JSON.toJSONString(infoItem.getWx_info()));

            deviceInfoMapper.insert(deviceInfoPo);
        }
    }
}
