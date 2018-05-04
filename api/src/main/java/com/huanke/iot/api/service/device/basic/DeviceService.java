package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.constants.Constants;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.api.vo.SpeedConfigRequest;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.DeviceRelationMapper;
import com.huanke.iot.base.dao.impl.device.DeviceTypeMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DeviceService {

    @Autowired
    DeviceGroupMapper deviceGroupMapper;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    DeviceSensorDataMapper deviceSensorDataMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private DeviceRelationMapper deviceRelationMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MqttSendService mqttSendService;


    public DeviceListVo obtainMyDevice(Integer userId) {
        DeviceListVo deviceListVo = new DeviceListVo();

        DeviceGroupPo queryDevicePo = new DeviceGroupPo();
        queryDevicePo.setUserId(userId);

        List<DeviceGroupPo> deviceGroupPos = deviceGroupMapper.selectList(queryDevicePo, 100000, 0);

        List<DeviceListVo.DeviceGroupData> groupDatas = deviceGroupPos.stream().map(
                deviceGroupPo -> {
                    DeviceListVo.DeviceGroupData deviceGroupData = new DeviceListVo.DeviceGroupData();
                    deviceGroupData.setGroupName(deviceGroupPo.getGroupName());
                    deviceGroupData.setGroupId(deviceGroupPo.getId());

                    String icon = deviceGroupPo.getIcon();
                    if (StringUtils.isEmpty(icon)) {
                        icon = Constants.DEFAULT_ICON;
                    }

                    String videoUrl = deviceGroupPo.getVideoUrl();
                    if (StringUtils.isEmpty(videoUrl)) {
                        videoUrl = Constants.DEFAULT_VIDEO_URl;
                    }

                    String videoCover = deviceGroupData.getVideoCover();
                    if (StringUtils.isEmpty(videoCover)) {
                        videoCover = Constants.DEFAULT_COVER;
                    }

                    String memo = deviceGroupPo.getMemo();
                    if (StringUtils.isEmpty(memo)) {
                        memo = Constants.MEMO;
                    }

                    deviceGroupData.setMemo(memo);
                    deviceGroupData.setVideoUrl(videoUrl);
                    deviceGroupData.setVideoCover(videoCover);
                    deviceGroupData.setIcon(icon);

                    DeviceGroupItemPo queryDeviceGroupItem = new DeviceGroupItemPo();
                    queryDeviceGroupItem.setUserId(userId);
                    queryDeviceGroupItem.setStatus(1);
                    queryDeviceGroupItem.setGroupId(deviceGroupData.getGroupId());
                    List<DeviceGroupItemPo> itemPos = deviceGroupMapper.queryGroupItems(queryDeviceGroupItem);
                    List<DeviceListVo.DeviceItemPo> deviceItemPos = itemPos.stream().filter(deviceGroupItemPo -> {
                        if (deviceGroupItemPo.getIsMaster() == 2) {
                            //分享绑定的
                            DeviceRelationPo deviceRelationPo = new DeviceRelationPo();
                            deviceRelationPo.setDeviceId(deviceGroupItemPo.getDeviceId());
                            deviceRelationPo.setStatus(1);
                            deviceRelationPo.setJoinUserId(deviceGroupItemPo.getUserId());
                            Integer count = deviceRelationMapper.selectCount(deviceRelationPo);
                            if (count == 0) {
                                return false;
                            }
                        }
                        return true;
                    }).map(deviceGroupItemPo -> {
                        DeviceListVo.DeviceItemPo deviceItemPo = new DeviceListVo.DeviceItemPo();
                        DevicePo devicePo = deviceMapper.selectById(deviceGroupItemPo.getDeviceId());
                        deviceItemPo.setDeviceId(devicePo.getDeviceId());
                        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(devicePo.getDeviceTypeId());
                        deviceItemPo.setOnlineStatus(1);
                        deviceItemPo.setDeviceName(devicePo.getName() == null ? "默认名称" : devicePo.getName());
                        if (deviceTypePo != null) {
                            deviceItemPo.setDeviceTypeName(deviceTypePo.getName());
                            deviceItemPo.setIcon(deviceTypePo.getIcon());
                        }
                        Map<Object, Object> data = stringRedisTemplate.opsForHash().entries("sensor." + devicePo.getId());
                        deviceItemPo.setPm(getData(data, SensorTypeEnums.PM25_IN.getCode()));
                        deviceItemPo.setCo2(getData(data, SensorTypeEnums.CO2_IN.getCode()));
                        deviceItemPo.setHum(getData(data, SensorTypeEnums.HUMIDITY_IN.getCode()));
                        deviceItemPo.setTem(getData(data, SensorTypeEnums.TEMPERATURE_IN.getCode()));
                        deviceItemPo.setHcho(getData(data, SensorTypeEnums.HCHO_IN.getCode()));
                        deviceItemPo.setTvoc(getData(data, SensorTypeEnums.TVOC_IN.getCode()));

                        return deviceItemPo;
                    }).collect(Collectors.toList());
                    deviceGroupData.setDeviceItemPos(deviceItemPos);
                    return deviceGroupData;
                }
        ).collect(Collectors.toList());

        deviceListVo.setGroupDataList(groupDatas);
        return deviceListVo;
    }

    private String getData(Map<Object, Object> data, String key) {
        if (data == null) {
            return "0";
        }
        Object storeVal = data.get(key);
        if (storeVal == null) {
            return "0";
        }
        return (String) storeVal;
    }


    public boolean editDevice(Integer userId, String deviceId, String deviceName) {
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if (devicePo == null) {
            return false;
        }
        DeviceGroupItemPo deviceGroupItemPo = new DeviceGroupItemPo();
        deviceGroupItemPo.setUserId(userId);
        deviceGroupItemPo.setStatus(1);
        deviceGroupItemPo.setDeviceId(devicePo.getId());
        Integer count = deviceGroupMapper.queryItemCount(deviceGroupItemPo);
        if (count == null || count == 0) {
            return false;
        }
        DevicePo updatePo = new DevicePo();
        updatePo.setDeviceId(deviceId);
        updatePo.setName(deviceName);
        return deviceMapper.updateByDeviceId(updatePo) > 0;
    }

    public Boolean setSpeedConfig(SpeedConfigRequest request) {
        String deviceIdStr = request.getDeviceStr();
        if (StringUtils.isEmpty(deviceIdStr)) {
            return false;
        }
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceIdStr);
        if (devicePo == null) {
            return false;
        }
        Integer deviceId = devicePo.getId();
        JSONObject config = new JSONObject();
        config.put("in", request.getInSpeed());
        config.put("out", request.getOutSpeed());
        DevicePo updatePo = new DevicePo();
        updatePo.setId(deviceId);
        updatePo.setSpeedConfig(config.toJSONString());
        deviceMapper.updateById(updatePo);

        int length = request.getInSpeed().size() * 2 + request.getOutSpeed().size() * 2;
        ByteBuf byteBuf = Unpooled.buffer(2 + length);
        byteBuf.writeShort(length);
        request.getInSpeed().forEach(speed -> {
            byteBuf.writeShort(speed);
        });
        request.getOutSpeed().forEach(speed -> {
            byteBuf.writeShort(speed);
        });
        String topic = "/down/cfg/" + deviceId;
        mqttSendService.sendMessage(topic, byteBuf.array());
        return true;
    }
}
