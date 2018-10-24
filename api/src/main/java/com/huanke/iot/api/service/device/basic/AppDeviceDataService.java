package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.huanke.iot.api.constants.Constants;
import com.huanke.iot.api.constants.DeviceAbilityTypeContants;
import com.huanke.iot.api.controller.app.response.AppDeviceDataVo;
import com.huanke.iot.api.controller.app.response.AppDeviceListVo;
import com.huanke.iot.api.controller.h5.response.DeviceAbilitysVo;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.dao.format.WxFormatMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.base.po.format.WxFormatPo;
import com.huanke.iot.base.util.LocationUtils;
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

@Repository
@Slf4j
public class AppDeviceDataService {


    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private WxFormatMapper wxFormatMapper;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;
    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;
    @Autowired
    private CustomerUserMapper customerUserMapper;
    @Autowired
    private DeviceTeamMapper deviceTeamMapper;
    @Autowired
    private LocationUtils locationUtils;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelAbilityOptionMapper;
    @Autowired
    private DeviceModelAbilityMapper deviceModelAbilityMapper;
    @Value("${unit}")
    private Integer unit;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${speed}")
    private int speed;
    @Value("${oss.url}")
    private String ossUrl;


    public AppDeviceListVo obtainMyDevice(Integer userId) {

        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        CustomerPo customerPo = customerMapper.selectById(customerUserPo.getCustomerId());
        AppDeviceListVo deviceListVo = new AppDeviceListVo();

        DeviceTeamPo queryDevicePo = new DeviceTeamPo();
        queryDevicePo.setMasterUserId(userId);
        queryDevicePo.setStatus(CommonConstant.STATUS_YES);
        List<DeviceTeamPo> deviceTeamPos = deviceTeamMapper.selectList(queryDevicePo, 100000, 0);

        List<AppDeviceListVo.DeviceTeamData> teamDatas = deviceTeamPos.stream().map(
                deviceTeamPo -> {
                    AppDeviceListVo.DeviceTeamData deviceTeamData = new AppDeviceListVo.DeviceTeamData();
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
                    List<AppDeviceListVo.DeviceItemPo> deviceItemPos = itemPos.stream().map(deviceTeamItemPo -> {
                        AppDeviceListVo.DeviceItemPo deviceItemPo = new AppDeviceListVo.DeviceItemPo();
                        DevicePo devicePo = deviceMapper.selectById(deviceTeamItemPo.getDeviceId());
                        deviceItemPo.setDeviceId(devicePo.getId());
                        deviceItemPo.setMac(devicePo.getMac());
                        deviceItemPo.setWxDeviceId(devicePo.getWxDeviceId());
                        int childDeviceCount = deviceMapper.queryChildDeviceCount(devicePo.getId());
                        deviceItemPo.setChildDeviceCount(childDeviceCount);
                        Integer modelId = devicePo.getModelId();
                        DeviceModelPo deviceModelPo = deviceModelMapper.selectById(modelId);
                        deviceItemPo.setDeviceModelName(deviceModelPo.getName());
                        Integer androidFormatId = deviceModelPo.getAndroidFormatId();
                        if(androidFormatId != null) {
                            WxFormatPo wxFormatPo = wxFormatMapper.selectById(androidFormatId);
                            deviceItemPo.setAndroidFormatId(wxFormatPo.getVersion());
                            deviceItemPo.setAndroidFormatName(wxFormatPo.getName());
                        }
                        Integer typeId = deviceModelPo.getTypeId();
                        deviceItemPo.setTypeId(typeId);
                        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
                        deviceItemPo.setTypeNo(deviceTypePo.getTypeNo());
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


    public List<AppDeviceDataVo> queryDetailAbilitysValue(Integer deviceId, List<Integer> abilityIds){
        Integer modelId = deviceMapper.selectById(deviceId).getModelId();
        List<AppDeviceDataVo> deviceAbilitysVoList = new ArrayList<>();
        Map<Object, Object> datas = stringRedisTemplate.opsForHash().entries("sensor2." + deviceId);
        Map<Object, Object> controlDatas = stringRedisTemplate.opsForHash().entries("control2." + deviceId);
        if(abilityIds == null || abilityIds.size()<1){
            abilityIds = new ArrayList<Integer>();
            List<DeviceModelAbilityPo> deviceModelAbilityPos = deviceModelAbilityMapper.selectByModelId(modelId);
            if(deviceModelAbilityPos != null && deviceModelAbilityPos.size()>0){
                for(DeviceModelAbilityPo temp : deviceModelAbilityPos){
                    abilityIds.add(temp.getAbilityId());
                }
            }else{
                return null;
            }
        }
        for (Integer abilityId : abilityIds) {
            DeviceAbilityPo deviceabilityPo = deviceAbilityMapper.selectById(abilityId);
            String dirValue = deviceabilityPo.getDirValue();
            Integer abilityType = deviceabilityPo.getAbilityType();
            AppDeviceDataVo deviceAbilitysVo = new AppDeviceDataVo();
            deviceAbilitysVo.setAbilityName(deviceabilityPo.getAbilityName());
            deviceAbilitysVo.setId(abilityId);
            deviceAbilitysVo.setAbilityType(abilityType);
            deviceAbilitysVo.setDirValue(dirValue);
            switch (abilityType) {
                case DeviceAbilityTypeContants.ability_type_text:
                    deviceAbilitysVo.setCurrValue(getData(datas, dirValue));
                    deviceAbilitysVo.setUnit(deviceabilityPo.getRemark());
                    break;
                case DeviceAbilityTypeContants.ability_type_single:
                    List<DeviceAbilityOptionPo> deviceabilityOptionPos = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                    String optionValue = getData(controlDatas, dirValue);
                    List<DeviceAbilitysVo.abilityOption> abilityOptionList = new ArrayList<>();
                    for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos) {
                        DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionMapper.queryByUnionModelAbility(modelId, abilityId, deviceabilityOptionPo.getId());
                        if(deviceModelAbilityOptionPo == null){
                            continue;
                        }
                        DeviceAbilitysVo.abilityOption abilityOption = new DeviceAbilitysVo.abilityOption();
                        abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                        if (optionValue.equals(deviceabilityOptionPo.getOptionValue())) {
                            deviceAbilitysVo.setCurrValue(deviceabilityOptionPo.getOptionValue());
                            abilityOption.setIsSelect(1);
                        } else {
                            abilityOption.setIsSelect(0);
                        }
                        abilityOptionList.add(abilityOption);
                    }
                    deviceAbilitysVo.setAbilityOptionList(abilityOptionList);
                    break;
                case DeviceAbilityTypeContants.ability_type_checkbox:
                    List<DeviceAbilityOptionPo> deviceabilityOptionPos1 = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                    List<DeviceAbilitysVo.abilityOption> abilityOptionList1 = new ArrayList<>();
                    for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos1) {
                        DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionMapper.queryByUnionModelAbility(modelId, abilityId, deviceabilityOptionPo.getId());
                        if(deviceModelAbilityOptionPo == null){
                            continue;
                        }
                        String targetOptionValue = deviceabilityOptionPo.getOptionValue();
                        String finalOptionValue = getData(controlDatas, targetOptionValue);
                        DeviceAbilitysVo.abilityOption abilityOption = new DeviceAbilitysVo.abilityOption();
                        abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                        if (Integer.valueOf(finalOptionValue) == 1) {
                            abilityOption.setIsSelect(1);
                        } else {
                            abilityOption.setIsSelect(0);
                        }
                        abilityOptionList1.add(abilityOption);
                    }
                    deviceAbilitysVo.setAbilityOptionList(abilityOptionList1);
                    break;
                case DeviceAbilityTypeContants.ability_type_threshhold:
                    deviceAbilitysVo.setCurrValue(getData(controlDatas, dirValue));
                    deviceAbilitysVo.setUnit(deviceabilityPo.getRemark());
                    break;
                case DeviceAbilityTypeContants.ability_type_threshholdselect:
                    DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(abilityId);
                    if (deviceAbilityPo.getDirValue().equals("-1")) {//滤网临时妥协办法，后期再想更优方式
                        List<DeviceAbilityOptionPo> deviceabilityOptionPos5 = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                        List<DeviceAbilitysVo.abilityOption> abilityOptionList5 = new ArrayList<>();
                        for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos5) {
                            String optionValue5 = getData(controlDatas, deviceabilityOptionPo.getOptionValue());
                            DeviceAbilitysVo.abilityOption abilityOption = new DeviceAbilitysVo.abilityOption();
                            abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                            abilityOption.setCurrValue(optionValue5);
                            abilityOptionList5.add(abilityOption);
                        }
                        deviceAbilitysVo.setAbilityOptionList(abilityOptionList5);
                    } else {
                        List<DeviceAbilityOptionPo> deviceabilityOptionPos5 = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                        String optionValue5 = getData(controlDatas, dirValue);
                        List<DeviceAbilitysVo.abilityOption> abilityOptionList5 = new ArrayList<>();
                        for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos5) {
                            DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionMapper.queryByUnionModelAbility(modelId, abilityId, deviceabilityOptionPo.getId());
                            if(deviceModelAbilityOptionPo == null){
                                continue;
                            }
                            DeviceAbilitysVo.abilityOption abilityOption = new DeviceAbilitysVo.abilityOption();
                            abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                            if (optionValue5.equals(deviceabilityOptionPo.getOptionValue())) {
                                abilityOption.setIsSelect(1);
                            } else {
                                abilityOption.setIsSelect(0);
                            }
                            abilityOptionList5.add(abilityOption);
                        }
                        deviceAbilitysVo.setAbilityOptionList(abilityOptionList5);
                    }
                    break;
                default:
                    break;

            }
            deviceAbilitysVoList.add(deviceAbilitysVo);
        }
        //添加空气质量判定
        if (datas.containsKey(SensorTypeEnums.PM25_IN.getCode())) {
            AppDeviceDataVo deviceAbilitysVo = new AppDeviceDataVo();
            deviceAbilitysVo.setDirValue("0");
            deviceAbilitysVo.setAbilityName("空气质量");

            String data = getData(datas, SensorTypeEnums.PM25_IN.getCode());
            if (StringUtils.isNotEmpty(data)) {
                Integer diData = Integer.valueOf(data);
                if (diData >= 0 && diData <= 35) {
                    deviceAbilitysVo.setCurrValue("优");
                } else if (diData > 35 && diData <= 75) {
                    deviceAbilitysVo.setCurrValue("良");
                } else if (diData > 75 && diData <= 150) {
                    deviceAbilitysVo.setCurrValue("中");
                } else {
                    deviceAbilitysVo.setCurrValue("差");
                }
            } else {
                deviceAbilitysVo.setCurrValue("优");
            }
            deviceAbilitysVoList.add(deviceAbilitysVo);
        }
        return deviceAbilitysVoList;
    }
    private String getData(Map<Object, Object> map, String key) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        }
        return "0";
    }
}
