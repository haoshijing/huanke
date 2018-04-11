package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.impl.device.data.DeviceLocationMapper;
import com.huanke.iot.base.po.device.data.DeviceLocationDataPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationHandler extends AbstractHandler {

    @Autowired
    private DeviceLocationMapper deviceLocationMapper;
    @Data
    public class GpsItem{
        private Integer lac;
        private Integer cid;
        private Integer rssi;
    }
    @Data
    public class  LocationItem{
        private String wifi;
        private String gps;
        private List<GpsItem> grps;
        private String blutooth;
        private String extFields;
    }
    @Override
    protected String getTopicType() {
        return "location";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        String message = new String(payloads);
        LocationItem locationItem = JSON.parseObject(message,LocationItem.class);
        DeviceLocationDataPo deviceLocationDataPo = new DeviceLocationDataPo();
        deviceLocationDataPo.setBlutooth(locationItem.getBlutooth());
        deviceLocationDataPo.setExtFields(locationItem.getExtFields());
        deviceLocationDataPo.setGps(locationItem.getGps());
        deviceLocationDataPo.setGrps(JSON.toJSONString(locationItem.getGrps()));
        deviceLocationDataPo.setDeviceId(getDeviceIdFromTopic(topic));
        deviceLocationMapper.insert(deviceLocationDataPo);
    }
}
