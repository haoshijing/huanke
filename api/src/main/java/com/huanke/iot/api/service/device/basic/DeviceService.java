package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.huanke.iot.api.constants.Constants;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.api.controller.h5.response.DeviceSpeedConfigVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.api.vo.SpeedConfigRequest;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.DeviceRelationMapper;
import com.huanke.iot.base.dao.impl.device.DeviceTypeMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.*;
import com.huanke.iot.base.util.LocationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${oss.url}")
    private String ossUrl;

    @Autowired
    private LocationUtils locationUtils;

    @Autowired
    private MqttSendService mqttSendService;

    @Value("${speed}")
    private int speed;

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
                    String qrcode = deviceGroupPo.getQrcode();
                    if (StringUtils.isEmpty(qrcode)) {
                        qrcode = "https://idcfota.oss-cn-hangzhou.aliyuncs.com/group/WechatIMG4213.jpeg";
                    }
                    List<String> adImages = Lists.newArrayList();
                    String adImageStr = deviceGroupPo.getAdImages();
                    if (StringUtils.isNotEmpty(adImageStr)) {
                        String adImageStrArr[] = adImageStr.split(",");
                        for (String adImage : adImageStrArr) {
                            if (StringUtils.isNotEmpty(adImage) && adImage.startsWith("http")) {
                                adImages.add(adImage);
                            } else {
                                adImages.add(ossUrl + "/" + adImage);
                            }
                        }
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
                    deviceGroupData.setQrcode(qrcode);
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
                        deviceItemPo.setOnlineStatus(devicePo.getOnlineStatus());
                        deviceItemPo.setDeviceName(devicePo.getName() == null ? "默认名称" : devicePo.getName());
                        if (deviceTypePo != null) {
                            deviceItemPo.setDeviceTypeName(deviceTypePo.getName());
                            deviceItemPo.setIcon(deviceTypePo.getIcon());
                        }
                        if (StringUtils.isEmpty(devicePo.getLocation())) {
                            JSONObject locationJson = locationUtils.getLocation(devicePo.getIp(), false);
                            if (locationJson != null) {
                                if (locationJson.containsKey("content")) {
                                    JSONObject content = locationJson.getJSONObject("content");
                                    if (content != null) {
                                        if (content.containsKey("address_detail")) {
                                            JSONObject addressDetail = content.getJSONObject("address_detail");
                                            if (addressDetail != null) {
                                                deviceItemPo.setLocation(addressDetail.getString("province") + "," + addressDetail.getString("city"));
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            String[] locationArray = devicePo.getLocation().split(",");
                            deviceItemPo.setLocation(Joiner.on(" ").join(locationArray));
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
        String deviceIdStr = request.getDeviceId();
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

    public DeviceSpeedConfigVo getSpeed(String deviceId) {
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if (devicePo == null) {
            return null;
        }
        DeviceSpeedConfigVo deviceSpeedConfigVo = new DeviceSpeedConfigVo();
        String config = devicePo.getSpeedConfig();
        if (StringUtils.isEmpty(config)) {
            List<DeviceSpeedConfigVo.SpeedConfigItem> inItems = Lists.newArrayList();
            for (int i = 1; i <= speed; i++) {
                inItems.add(new DeviceSpeedConfigVo.SpeedConfigItem(i, 10 * i));
            }

            List<DeviceSpeedConfigVo.SpeedConfigItem> outItems = Lists.newArrayList();
            for (int i = 1; i <= speed; i++) {
                outItems.add(new DeviceSpeedConfigVo.SpeedConfigItem(i, 10 * i));
            }
            deviceSpeedConfigVo.setInItems(inItems);
            deviceSpeedConfigVo.setOutItems(outItems);
        } else {
            JSONObject jsonObject = JSON.parseObject(devicePo.getSpeedConfig());
            JSONArray inArray = jsonObject.getJSONArray("in");
            List<DeviceSpeedConfigVo.SpeedConfigItem> inItems = Lists.newArrayList();
            List<DeviceSpeedConfigVo.SpeedConfigItem> outItems = Lists.newArrayList();
            if (inArray != null) {
                for (int i = 0; i < inArray.size(); i++) {
                    DeviceSpeedConfigVo.SpeedConfigItem item = new DeviceSpeedConfigVo.SpeedConfigItem();
                    item.setLevel(i + 1);
                    item.setSpeed(inArray.getInteger(i));
                    inItems.add(item);
                }
            }

            JSONArray outArray = jsonObject.getJSONArray("out");
            if (outArray != null) {
                for (int i = 0; i < outArray.size(); i++) {
                    DeviceSpeedConfigVo.SpeedConfigItem item = new DeviceSpeedConfigVo.SpeedConfigItem();
                    item.setLevel(i + 1);
                    item.setSpeed(outArray.getInteger(i));
                    outItems.add(item);
                }
            } else {
                outItems = Lists.newArrayList();
                for (int i = 1; i <= speed; i++) {
                    outItems.add(new DeviceSpeedConfigVo.SpeedConfigItem(i, 10 * i));
                }
            }
            deviceSpeedConfigVo.setOutItems(outItems);
            deviceSpeedConfigVo.setInItems(inItems);
        }
        return deviceSpeedConfigVo;
    }

    public Boolean editDeviceLoc(Integer userId, String deviceId, String location) {
        DevicePo devicePo = new DevicePo();
        devicePo.setDeviceId(deviceId);
        devicePo.setLocation(location);
        return deviceMapper.updateByDeviceId(devicePo) > 0;
    }
}
