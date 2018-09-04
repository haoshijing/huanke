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
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceCustomerUserRelationMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.base.util.LocationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * update by onlymark
 * 2018/8/20
 */
@Repository
@Slf4j
public class DeviceService {

    @Autowired
    DeviceTeamMapper deviceTeamMapper;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;


    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private DeviceCustomerUserRelationMapper deviceCustomerUserRelationMapper;

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

        DeviceTeamPo queryDevicePo = new DeviceTeamPo();
        queryDevicePo.setMasterUserId(userId);
        List<DeviceTeamPo> deviceTeamPos = deviceTeamMapper.selectList(queryDevicePo, 100000, 0);

        List<DeviceListVo.DeviceTeamData> groupDatas = deviceTeamPos.stream().map(
                deviceTeamPo -> {
                    DeviceListVo.DeviceTeamData deviceTeamData = new DeviceListVo.DeviceTeamData();
                    deviceTeamData.setTeamName(deviceTeamPo.getName());
                    deviceTeamData.setTeamId(deviceTeamPo.getId());

                    String icon = deviceTeamPo.getIcon();
                    if (StringUtils.isEmpty(icon)) {
                        icon = Constants.DEFAULT_ICON;
                    }
                    String qrcode = deviceTeamPo.getQrcode();
                    if (StringUtils.isEmpty(qrcode)) {
                        qrcode = "https://idcfota.oss-cn-hangzhou.aliyuncs.com/group/WechatIMG4213.jpeg";
                    }
                    List<String> adImages = Lists.newArrayList();
                    String adImageStr = deviceTeamPo.getAdImages();
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
                    deviceTeamData.setAdImages(adImages);
                    String videoUrl = deviceTeamPo.getVideoUrl();
                    if (StringUtils.isEmpty(videoUrl)) {
                        videoUrl = Constants.DEFAULT_VIDEO_URl;
                    }

                    String videoCover = deviceTeamPo.getVideoCover();
                    if (StringUtils.isEmpty(videoCover)) {
                        videoCover = Constants.DEFAULT_COVER;
                    }

                    String memo = deviceTeamPo.getSceneDescription();
                    if (StringUtils.isEmpty(memo)) {
                        memo = Constants.MEMO;
                    }

                    deviceTeamData.setMemo(memo);
                    deviceTeamData.setVideoUrl(videoUrl);
                    deviceTeamData.setVideoCover(videoCover);
                    deviceTeamData.setIcon(icon);
                    deviceTeamData.setQrcode(qrcode);
                    DeviceTeamItemPo queryDeviceTeamItem = new DeviceTeamItemPo();
                    queryDeviceTeamItem.setStatus(1);
                    queryDeviceTeamItem.setTeamId(deviceTeamData.getTeamId());
                    List<DeviceTeamItemPo> itemPos = deviceTeamMapper.queryTeamItems(queryDeviceTeamItem);
                    List<DeviceListVo.DeviceItemPo> deviceItemPos = itemPos.stream().map(deviceTeamItemPo -> {
                        DeviceListVo.DeviceItemPo deviceItemPo = new DeviceListVo.DeviceItemPo();
                        DevicePo devicePo = deviceMapper.selectById(deviceTeamItemPo.getDeviceId());
                        deviceItemPo.setDeviceId(devicePo.getDeviceId());
                        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(devicePo.getTypeId());
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
                    deviceTeamData.setDeviceItemPos(deviceItemPos);
                    return deviceTeamData;
                }
        ).collect(Collectors.toList());

        deviceListVo.setTeamDataList(groupDatas);
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
            log.error("找不到设备，deviceId={}", deviceId);
            return false;
        }
        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
        deviceCustomerUserRelationPo.setOpenId(customerUserPo.getOpenId());
        deviceCustomerUserRelationPo.setDeviceId(devicePo.getId());
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPos = deviceCustomerUserRelationMapper.findAllByDeviceCustomerUserRelationPo(deviceCustomerUserRelationPo);
        int count = deviceCustomerUserRelationPos.size();
        if (count == 0) {
            log.error("找不到设备用户对应关系，deviceId={}，openId={}", deviceId, customerUserPo.getOpenId());
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
            log.error("参数错误");
            return false;
        }
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceIdStr);
        if (devicePo == null) {
            log.error("找不到设备，deviceIdStr={}", deviceIdStr);
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
            log.error("找不到设备，deviceId={}", deviceId);
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
