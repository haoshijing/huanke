package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.huanke.iot.api.constants.Constants;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.api.controller.h5.response.DeviceSpeedConfigVo;
import com.huanke.iot.api.controller.h5.response.LocationVo;
import com.huanke.iot.api.controller.h5.response.WeatherVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.api.vo.SpeedConfigRequest;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceCustomerUserRelationMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.dao.format.WxFormatMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.customer.CustomerPo;
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

import java.util.ArrayList;
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
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private CustomerMapper customerMapper;

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

    @Autowired
    private WxFormatMapper wxFormatMapper;

    @Value("${speed}")
    private int speed;

    public DeviceListVo obtainMyDevice(Integer userId) {

        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        CustomerPo customerPo = customerMapper.selectById(customerUserPo.getCustomerId());
        DeviceListVo deviceListVo = new DeviceListVo();

        DeviceTeamPo queryDevicePo = new DeviceTeamPo();
        queryDevicePo.setMasterUserId(userId);
        queryDevicePo.setStatus(CommonConstant.STATUS_YES);
        List<DeviceTeamPo> deviceTeamPos = deviceTeamMapper.selectList(queryDevicePo, 100000, 0);

        List<DeviceListVo.DeviceTeamData> teamDatas = deviceTeamPos.stream().map(
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
                        deviceItemPo.setDeviceId(devicePo.getId());
                        deviceItemPo.setWxDeviceId(devicePo.getWxDeviceId());
                        int childDeviceCount = deviceMapper.queryChildDeviceCount(devicePo.getId());
                        deviceItemPo.setChildDeviceCount(childDeviceCount);
                        Integer modelId = devicePo.getModelId();
                        deviceItemPo.setFormatName(wxFormatMapper.selectById(deviceModelMapper.selectById(modelId).getFormatId()).getName());
                        Integer typeId = deviceModelMapper.selectById(modelId).getTypeId();
                        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
                        deviceItemPo.setOnlineStatus(devicePo.getOnlineStatus());

                        //添加返回客户名称
                        deviceItemPo.setCustomerName(customerPo.getName());
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
                        Map<Object, Object> data = stringRedisTemplate.opsForHash().entries("sensor2." + devicePo.getId());
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

        deviceListVo.setTeamDataList(teamDatas);
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


    public boolean editDevice(Integer userId, Integer deviceId, String deviceName) {
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo == null) {
            log.error("找不到设备，deviceId={}", deviceId);
            return false;
        }
        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        DeviceCustomerUserRelationPo querydeviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
        querydeviceCustomerUserRelationPo.setOpenId(customerUserPo.getOpenId());
        querydeviceCustomerUserRelationPo.setDeviceId(devicePo.getId());
        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = deviceCustomerUserRelationMapper.findAllByDeviceCustomerUserRelationPo(querydeviceCustomerUserRelationPo);
        if (deviceCustomerUserRelationPo == null) {
            log.error("找不到设备用户对应关系，wxDeviceId={}，openId={}", deviceId, customerUserPo.getOpenId());
            return false;
        }
        //
        DevicePo updatePo = new DevicePo();
        updatePo.setId(deviceId);
        updatePo.setName(deviceName);
        return deviceMapper.updateById(updatePo) > 0;
    }

    public Boolean setSpeedConfig(SpeedConfigRequest request) {
        String deviceIdStr = request.getDeviceId();
        if (StringUtils.isEmpty(deviceIdStr)) {
            log.error("参数错误");
            return false;
        }
        DevicePo devicePo = deviceMapper.selectByWxDeviceId(deviceIdStr);
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
        DevicePo devicePo = deviceMapper.selectByWxDeviceId(deviceId);
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

    public Boolean editDeviceLoc(Integer userId, Integer deviceId, String location) {
        DevicePo devicePo = new DevicePo();
        devicePo.setId(deviceId);
        devicePo.setLocation(location);
        return deviceMapper.updateById(devicePo) > 0;
    }


    public void deleteById(Integer childDeviceId) {
        deviceMapper.deleteById(childDeviceId);
    }

    public LocationVo queryDeviceLocation(Integer deviceId) {
        LocationVo locationVo = new LocationVo();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (StringUtils.isEmpty(devicePo.getLocation())) {
            JSONObject locationJson = locationUtils.getLocation(devicePo.getIp(), true);
            if (locationJson != null) {
                if (locationJson.containsKey("content")) {
                    JSONObject content = locationJson.getJSONObject("content");
                    if (content != null) {
                        if (content.containsKey("address_detail")) {
                            JSONObject addressDetail = content.getJSONObject("address_detail");
                            if (addressDetail != null) {
                                locationVo.setProvince(addressDetail.getString("province"));
                                locationVo.setCity(addressDetail.getString("city"));
                                locationVo.setArea(locationVo.getCity());
                                locationVo.setLocation(locationVo.getProvince() + "," + locationVo.getCity());
                            }
                        }

                    }
                }
            }
        } else {
            String[] locationArray = devicePo.getLocation().split(",");
            locationVo.setArea(Joiner.on(" ").join(locationArray));
            locationVo.setLocation(devicePo.getLocation());
        }
        return locationVo;
    }

    public WeatherVo queryDeviceWeather(Integer deviceId) {
        WeatherVo weatherVo = new WeatherVo();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        JSONObject weatherJson = locationUtils.getWeather(devicePo.getIp(), true);
        if (weatherJson != null) {
            if (weatherJson.containsKey("result")) {
                JSONObject result = weatherJson.getJSONObject("result");
                if (result != null) {
                    weatherVo.setOuterHum(result.getString("humidity"));
                    weatherVo.setOuterPm(result.getString("aqi"));
                    weatherVo.setOuterTem(result.getString("temperature_curr"));
                    weatherVo.setWeather(result.getString("weather_curr"));
                }
            }
        }
        return weatherVo;
    }

    public List<DeviceListVo.DeviceItemPo> queryChildDevice(Integer hostDeviceId) {
        List<DeviceListVo.DeviceItemPo> childDevicePos = new ArrayList<>();
        List<DevicePo> devicePoList = deviceMapper.queryChildDevice(hostDeviceId);
        for (DevicePo devicePo : devicePoList) {
            DeviceListVo.DeviceItemPo deviceItemPo = new DeviceListVo.DeviceItemPo();
            deviceItemPo.setDeviceId(devicePo.getId());
            deviceItemPo.setWxDeviceId(devicePo.getWxDeviceId());
            deviceItemPo.setLocation(devicePo.getLocation());
            Integer modelId = devicePo.getModelId();
            Integer typeId = deviceModelMapper.selectById(modelId).getTypeId();
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
            deviceItemPo.setOnlineStatus(devicePo.getOnlineStatus());
            deviceItemPo.setDeviceName(devicePo.getName() == null ? "默认名称" : devicePo.getName());
            deviceItemPo.setOnlineStatus(devicePo.getOnlineStatus());
            if (deviceTypePo != null) {
                deviceItemPo.setDeviceTypeName(deviceTypePo.getName());
                deviceItemPo.setIcon(deviceTypePo.getIcon());
            }
            Map<Object, Object> data = stringRedisTemplate.opsForHash().entries("sensor2." + devicePo.getId());
            deviceItemPo.setPm(getData(data, SensorTypeEnums.PM25_IN.getCode()));
            deviceItemPo.setCo2(getData(data, SensorTypeEnums.CO2_IN.getCode()));
            deviceItemPo.setHum(getData(data, SensorTypeEnums.HUMIDITY_IN.getCode()));
            deviceItemPo.setTem(getData(data, SensorTypeEnums.TEMPERATURE_IN.getCode()));
            deviceItemPo.setHcho(getData(data, SensorTypeEnums.HCHO_IN.getCode()));
            deviceItemPo.setTvoc(getData(data, SensorTypeEnums.TVOC_IN.getCode()));
            childDevicePos.add(deviceItemPo);
        }
        return childDevicePos;
    }
}
