package com.huanke.iot.gateway.io.impl;

import com.huanke.iot.base.dao.device.DeviceTeamItemMapper;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.gateway.io.AbstractHandler;
import com.huanke.iot.gateway.mqttlistener.MqttService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : sxj
 * @Date : 2018/12/4 8:28
 * @Version : 1.0
 */

@Repository
@Slf4j
public class ProfileHandler extends AbstractHandler {
    @Autowired
    private DeviceTeamItemMapper deviceTeamItemMapper;
    @Autowired
    private MqttService mqttService;
    @Data
    public static class FuncItemMessage{
        private String type;
        private Integer value;
        private String childid;
    }

    @Data
    public static class FuncListMessage{
        private Integer msg_id;
        private String msg_type;
        private List<ProfileHandler.FuncItemMessage> datas;
    }

    protected String getTopicType() {
        return "profile";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        //接收组内联动的topic
        Integer deviceId = getDeviceIdFromTopic(topic);
        //查找联动组内的其他设备
        DeviceTeamItemPo deviceTeamItemPo = this.deviceTeamItemMapper.selectByDeviceId(deviceId);
        if(null != deviceTeamItemPo && deviceTeamItemPo.getLinkAgeStatus().equals(1)){
            List<DeviceTeamItemPo> deviceTeamItemPoList = this.deviceTeamItemMapper.selectLinkDevice(deviceTeamItemPo);
            deviceTeamItemPoList.stream().forEach(eachPo ->{
                //获取当前设备的功能项

                String sendTopic = "/down2/profile/" + eachPo.getDeviceId();
                String message = "";
                mqttService.sendMessage(sendTopic,message);
            });
        }
    }
}
