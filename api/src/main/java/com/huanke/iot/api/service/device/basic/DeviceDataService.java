package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.huanke.iot.api.constants.DeviceAbilityTypeContants;
import com.huanke.iot.api.controller.h5.req.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.req.DeviceGroupFuncVo;
import com.huanke.iot.api.controller.h5.req.ShareRequest;
import com.huanke.iot.api.controller.h5.response.DeviceAbilitysVo;
import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.controller.h5.response.DeviceShareVo;
import com.huanke.iot.api.controller.h5.response.SensorDataVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.api.service.device.team.DeviceTeamService;
import com.huanke.iot.api.util.FloatDataUtil;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.base.util.LocationUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceDataService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private MqttSendService mqttSendService;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private DeviceGroupItemMapper deviceGroupItemMapper;

    @Autowired
    private DeviceIdPoolMapper deviceIdPoolMapper;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private DeviceCustomerUserRelationMapper deviceCustomerUserRelationMapper;

    @Autowired
    private DeviceTeamItemMapper deviceTeamItemMapper;

    @Autowired
    private DeviceTeamMapper deviceTeamMapper;

    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;

    @Autowired
    private WxConfigMapper wxConfigMapper;

    @Autowired
    private LocationUtils locationUtils;

    @Autowired
    private DeviceTeamService deviceTeamService;

    @Value("${unit}")
    private Integer unit;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static String[] modes = {"云端大数据联动", "神经网络算法", "模糊驱动算法", "深度学习算法"};

    private static final int MASTER = 1;
    private static final int SLAVE = 2;
    @Value("${speed}")
    private int speed;

    private static final String TOKEN_PREFIX = "token.";

    @Transactional
    public Object shareDevice(Integer toId, ShareRequest request) throws InvocationTargetException, IllegalAccessException {
        Integer deviceId = request.getDeviceId();
        String master = request.getMasterOpenId();
        String token = request.getToken();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        String deviceIdStr = devicePo.getWxDeviceId();
        if (devicePo == null) {
            log.error("找不到设备，deviceId={}", deviceId);
            return false;
        }
        //通过设备查customerId
        Integer customerId = deviceMapper.getCustomerId(devicePo);
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(master);
        if (customerUserPo == null) {
            log.error("找不到用户，openId={}", master);
            return false;
        }
        String storeToken = stringRedisTemplate.opsForValue().get(TOKEN_PREFIX + deviceIdStr);
        if (StringUtils.isEmpty(storeToken) || !StringUtils.equals(storeToken, token)) {
            log.error("找不到Token，deviceIdStr={}", deviceIdStr);
            return false;
        }
        if (customerUserPo.getId().equals(toId)) {
            log.error("无法给自己分享设备");
            return false;
        }
        //TODO检查deviceId和用户是不是可以对应上的
        DeviceTeamItemPo queryTeamItemPo = new DeviceTeamItemPo();
        queryTeamItemPo.setDeviceId(deviceId);
        queryTeamItemPo.setUserId(customerUserPo.getId());
        queryTeamItemPo.setStatus(1);
        Integer itemCount = deviceTeamMapper.queryItemCount(queryTeamItemPo);
        if (itemCount == 0) {
            log.error("用户组下无设备");
            return false;
        }

        /*List<DeviceTeamPo> deviceTeamPoList = deviceTeamService.selectByUserId(toId);
        if (!deviceTeamPoList.isEmpty()) {
            List<DeviceTeamVo> deviceTeamVoList = new ArrayList<>();
            for (DeviceTeamPo deviceTeamPo : deviceTeamPoList) {
                DeviceTeamVo deviceTeamVo = new DeviceTeamVo();
                deviceTeamVo.setId(deviceTeamPo.getId());
                deviceTeamVo.setName(deviceTeamPo.getName());
                deviceTeamVo.setIcon(deviceTeamPo.getIcon());
                deviceTeamVoList.add(deviceTeamVo);
            }
            return deviceTeamVoList;
        }*/

        DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
        String defaultTeamName = wxConfigMapper.selectConfigByCustomerId(customerId).getDefaultTeamName();
        deviceTeamPo.setName(defaultTeamName);
        deviceTeamPo.setMasterUserId(toId);
        deviceTeamPo.setCreateUserId(toId);
        deviceTeamPo.setCustomerId(customerUserPo.getCustomerId());
        deviceTeamPo.setStatus(1);
        deviceTeamPo.setCreateTime(System.currentTimeMillis());
        deviceTeamPo.setTeamStatus(1);
        deviceTeamPo.setTeamType(3);
        deviceTeamPo.setCreateUserId(toId);
        deviceTeamPo.setCustomerId(customerId);
        deviceTeamMapper.insert(deviceTeamPo);
        Integer teamId = deviceTeamPo.getId();


        DeviceTeamItemPo queryItemPo = new DeviceTeamItemPo();
        queryItemPo.setDeviceId(deviceId);
        queryItemPo.setUserId(toId);
        queryItemPo.setTeamId(teamId);
        queryItemPo.setStatus(1);
        queryItemPo.setUserId(toId);
        queryItemPo.setCreateTime(System.currentTimeMillis());
        deviceTeamItemMapper.insert(queryItemPo);

        return true;
    }

    public Boolean clearRelation(String joinOpenId, Integer userId, Integer deviceId) {
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo == null) {
            log.error("找不到设备，deviceId={}", deviceId);
            return false;
        }
        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
        deviceCustomerUserRelationPo.setOpenId(customerUserPo.getOpenId());
        deviceCustomerUserRelationPo.setDeviceId(deviceId);
        DeviceCustomerUserRelationPo byDeviceCustomerUserRelationPo = deviceCustomerUserRelationMapper.findAllByDeviceCustomerUserRelationPo(deviceCustomerUserRelationPo);
        if (byDeviceCustomerUserRelationPo == null) {
            log.error("操作人无权限，deviceId={}", deviceId);
            return false;
        }

        CustomerUserPo beClearCustomerUserPo = customerUserMapper.selectByOpenId(joinOpenId);
        if (beClearCustomerUserPo == null) {
            log.error("被删除用户不存在，deviceId={}", deviceId);
            return false;
        }
        if (beClearCustomerUserPo.getId().equals(userId)) {
            log.error("主管理员无法删除，deviceId={}", deviceId);
            return false;
        }

        DeviceTeamItemPo queryItemPo = new DeviceTeamItemPo();
        queryItemPo.setUserId(beClearCustomerUserPo.getId());
        queryItemPo.setDeviceId(deviceId);
        queryItemPo.setStatus(CommonConstant.STATUS_YES);
        List<DeviceTeamItemPo> deviceTeamItemPos = deviceTeamMapper.queryTeamItems(queryItemPo);
        if (deviceTeamItemPos.size() == 0) {
            log.error("被删除用户无此设备，deviceId={}", deviceId);
            return false;
        }
        DeviceTeamItemPo deviceTeamItemPo = deviceTeamItemPos.get(0);
        return deviceTeamItemMapper.updateStatus(deviceTeamItemPo.getDeviceId(), deviceTeamItemPo.getUserId(), CommonConstant.STATUS_DEL) > 0;
    }


    public List<SensorDataVo> getHistoryData(Integer deviceId, Integer type) {

        Long startTimestamp = new DateTime().plusDays(-1).getMillis();
        Long endTimeStamp = System.currentTimeMillis();

        List<SensorDataVo> sensorDataVos = Lists.newArrayList();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo == null) {
            return null;
        }
        Integer deviceTypeId = devicePo.getTypeId();
        List<String> dirValues = deviceAbilityMapper.getDirValuesByDeviceTypeId(deviceTypeId);

        List<DeviceSensorStatPo> deviceSensorPos = deviceSensorStatMapper.selectData(devicePo.getId(), startTimestamp, endTimeStamp);
        for (String sensorType : dirValues) {
            SensorDataVo sensorDataVo = new SensorDataVo();
            SensorTypeEnums sensorTypeEnums = SensorTypeEnums.getByCode(sensorType);
            sensorDataVo.setName(sensorTypeEnums.getMark());
            sensorDataVo.setUnit(sensorTypeEnums.getUnit());
            sensorDataVo.setType(sensorType);
            List<String> xdata = Lists.newArrayList();
            List<String> ydata = Lists.newArrayList();
            for (DeviceSensorStatPo deviceSensorPo : deviceSensorPos) {
                if (deviceSensorPo.getPm() == null) {
                    continue;
                }
                xdata.add(new DateTime(deviceSensorPo.getStartTime()).toString("yyyy-MM-dd HH:mm:ss"));
                if (StringUtils.equals(sensorType, SensorTypeEnums.CO2_IN.getCode())) {
                    ydata.add(deviceSensorPo.getCo2().toString());
                } else if (StringUtils.equals(sensorType, SensorTypeEnums.HUMIDITY_IN.getCode())) {
                    ydata.add(deviceSensorPo.getHum().toString());
                } else if (StringUtils.equals(sensorType, SensorTypeEnums.TEMPERATURE_IN.getCode())) {
                    ydata.add(deviceSensorPo.getTem().toString());
                } else if (StringUtils.equals(sensorType, SensorTypeEnums.HCHO_IN.getCode())) {
                    ydata.add(FloatDataUtil.getFloat(deviceSensorPo.getHcho()));
                } else if (StringUtils.equals(sensorType, SensorTypeEnums.PM25_IN.getCode())) {
                    if (deviceSensorPo.getPm() != null) {
                        ydata.add(deviceSensorPo.getPm().toString());
                    } else {
                        ydata.add("");
                    }
                } else if (StringUtils.equals(sensorType, SensorTypeEnums.TVOC_IN.getCode())) {
                    ydata.add(FloatDataUtil.getFloat(deviceSensorPo.getTvoc()));
                } else {
                    continue;
                }
                sensorDataVo.setXdata(xdata);
                sensorDataVo.setYdata(ydata);
            }
            if (!ydata.isEmpty()) {
                sensorDataVos.add(sensorDataVo);
            }
            sensorDataVo.setXdata(xdata);
            sensorDataVo.setYdata(ydata);
        }

        return sensorDataVos;
    }

    public List<DeviceShareVo> shareList(Integer userId, Integer deviceId) {
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo != null) {
            CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
            DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
            deviceCustomerUserRelationPo.setOpenId(customerUserPo.getOpenId());
            deviceCustomerUserRelationPo.setDeviceId(deviceId);
            DeviceCustomerUserRelationPo byDeviceCustomerUserRelationPo = deviceCustomerUserRelationMapper.findAllByDeviceCustomerUserRelationPo(deviceCustomerUserRelationPo);

            if (byDeviceCustomerUserRelationPo != null) {
                DeviceTeamItemPo deviceTeamItemPo = new DeviceTeamItemPo();
                deviceTeamItemPo.setDeviceId(deviceId);
                List<DeviceTeamItemPo> deviceTeamItemPos = deviceTeamItemMapper.selectItemsByDeviceId(deviceId);
                List<DeviceTeamItemPo> finalDeviceTeamItemPos = deviceTeamItemPos.stream().filter(deviceTeamItem -> deviceTeamItem.getStatus() == 1).sorted(Comparator.comparing(DeviceTeamItemPo::getCreateTime)).collect(Collectors.toList());
                List<DeviceShareVo> shareVos = finalDeviceTeamItemPos.stream()
                        .map(finalDeviceTeamItemPo -> {
                            Integer deviceUserId = finalDeviceTeamItemPo.getUserId();
                            CustomerUserPo deviceCustomerUserPo = customerUserMapper.selectById(deviceUserId);
                            DeviceShareVo deviceShareVo = new DeviceShareVo();
                            deviceShareVo.setUserId(deviceCustomerUserPo.getId());
                            deviceShareVo.setNickname(deviceCustomerUserPo.getNickname());
                            deviceShareVo.setJoinTime(finalDeviceTeamItemPo.getCreateTime());
                            deviceShareVo.setOpenId(deviceCustomerUserPo.getOpenId());
                            deviceShareVo.setHeadImg(deviceCustomerUserPo.getHeadimgurl());
                            return deviceShareVo;
                        }).collect(Collectors.toList());

                return shareVos;
            }
        }
        return Lists.newArrayList();
    }

    @Transactional
    public Boolean deleteDevice(Integer userId, Integer deviceId) {
        if (deviceId == null) {
            return false;
        }
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo == null) {
            return false;
        }
        Boolean ret = false;

        Integer iDeviceId = devicePo.getId();

        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        DeviceCustomerUserRelationPo querydeviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
        querydeviceCustomerUserRelationPo.setOpenId(customerUserPo.getOpenId());
        querydeviceCustomerUserRelationPo.setDeviceId(iDeviceId);
        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = deviceCustomerUserRelationMapper.findAllByDeviceCustomerUserRelationPo(querydeviceCustomerUserRelationPo);
        if (deviceCustomerUserRelationPo == null) {
            return false;
        }

        if (deviceCustomerUserRelationPo.getParentOpenId() == null) {
            //主控制人
            ret = deviceCustomerUserRelationMapper.deleteRelationByDeviceId(iDeviceId) > 0;
            ret = ret && deviceTeamItemMapper.deleteItemsByDeviceId(iDeviceId) > 0;
            //deviceGroupItemMapper.deleteByJoinId(iDeviceId, userId);
        } else {
            ret = deviceCustomerUserRelationMapper.deleteRelationByJoinId(customerUserPo.getOpenId(), iDeviceId) > 0;
            ret = ret && deviceTeamItemMapper.deleteByJoinId(iDeviceId, userId) > 0;
            //deviceGroupItemMapper.deleteByJoinId(iDeviceId, userId);
        }
        return ret;
    }

    /**
     * 查询设备功能项值
     *
     * @param abilityIds
     * @return
     */
    public List<DeviceAbilitysVo> queryDetailAbilitysValue(Integer deviceId, List<Integer> abilityIds) {
        List<DeviceAbilitysVo> deviceAbilitysVoList = new ArrayList<>();
        Map<Object, Object> datas = stringRedisTemplate.opsForHash().entries("sensor2." + deviceId);
        Map<Object, Object> controlDatas = stringRedisTemplate.opsForHash().entries("control2." + deviceId);
        for (Integer abilityId : abilityIds) {
            DeviceAbilityPo deviceabilityPo = deviceAbilityMapper.selectById(abilityId);
            String dirValue = deviceabilityPo.getDirValue();
            Integer abilityType = deviceabilityPo.getAbilityType();

            DeviceAbilitysVo deviceAbilitysVo = new DeviceAbilitysVo();
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
                        DeviceAbilitysVo.abilityOption abilityOption = new DeviceAbilitysVo.abilityOption();
                        abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                        if (optionValue.equals(deviceabilityOptionPo.getOptionValue())) {
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
                    List<DeviceAbilityOptionPo> deviceabilityOptionPos5 = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                    String optionValue5 = getData(controlDatas, dirValue);
                    List<DeviceAbilitysVo.abilityOption> abilityOptionList5 = new ArrayList<>();
                    for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos5) {
                        DeviceAbilitysVo.abilityOption abilityOption = new DeviceAbilitysVo.abilityOption();
                        abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                        abilityOption.setCurrValue(optionValue5);
                        abilityOptionList5.add(abilityOption);
                    }
                    deviceAbilitysVo.setAbilityOptionList(abilityOptionList5);
                    break;
                default:
                    break;

            }
            deviceAbilitysVoList.add(deviceAbilitysVo);
        }
        //添加空气质量判定
        if (datas.containsKey(SensorTypeEnums.PM25_IN.getCode())) {
            DeviceAbilitysVo deviceAbilitysVo = new DeviceAbilitysVo();
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


    @Data
    public static class FuncItemMessage {
        private String type;
        private String value;
        private String childid;

    }

    @Data
    public static class FuncListMessage {
        private String msg_id;
        private String msg_type;
        private List<DeviceDataService.FuncItemMessage> datas;
    }

    public DeviceDetailVo queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = new DeviceDetailVo();
        DevicePo devicePo = deviceMapper.selectByWxDeviceId(deviceId);

        if (devicePo != null) {
            deviceDetailVo.setDeviceName(devicePo.getName());
            deviceDetailVo.setDeviceId(deviceId);
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(devicePo.getTypeId());
            if (deviceTypePo != null) {
                deviceDetailVo.setDeviceTypeName(deviceTypePo.getName());
            }

            deviceDetailVo.setIp(devicePo.getIp());
            deviceDetailVo.setMac(devicePo.getMac());
            deviceDetailVo.setDate(new DateTime().toString("yyyy年MM月dd日"));
            getIndexData(deviceDetailVo, devicePo.getId(), devicePo.getTypeId());
            if (deviceDetailVo.getPm() == null || StringUtils.isEmpty(deviceDetailVo.getPm().getData()) || StringUtils.equals("0", deviceDetailVo.getPm().getData())) {
                deviceDetailVo.setAqi("0");
            } else {
                Integer pm = Integer.valueOf(deviceDetailVo.getPm().getData());
                deviceDetailVo.setAqi(String.valueOf(getAqi(pm)));
            }
            fillDeviceInfo(deviceDetailVo, devicePo);
        }

        JSONObject weatherJson = locationUtils.getWeather(devicePo.getIp(), false);
        if (weatherJson != null) {
            if (weatherJson.containsKey("result")) {
                JSONObject result = weatherJson.getJSONObject("result");
                if (result != null) {
                    deviceDetailVo.setOuterHum(result.getString("humidity"));
                    deviceDetailVo.setOuterPm(result.getString("aqi"));
                    deviceDetailVo.setOuterTem(result.getString("temperature_curr"));
                    deviceDetailVo.setWeather(result.getString("weather_curr"));
                }
            }
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
                                deviceDetailVo.setProvince(addressDetail.getString("province"));
                                deviceDetailVo.setCity(addressDetail.getString("city"));
                                deviceDetailVo.setArea(deviceDetailVo.getCity());
                                deviceDetailVo.setLocation(deviceDetailVo.getProvince() + "," + deviceDetailVo.getCity());
                            }
                        }

                    }
                }
            }
        } else {
            String[] locationArray = devicePo.getLocation().split(",");
            deviceDetailVo.setArea(Joiner.on(" ").join(locationArray));
            deviceDetailVo.setLocation(devicePo.getLocation());
        }
        return deviceDetailVo;
    }

    private void fillDeviceInfo(DeviceDetailVo deviceDetailVo, DevicePo devicePo) {
        DeviceDetailVo.DeviceInfoItem info = new DeviceDetailVo.DeviceInfoItem();
        info.setDeviceSupport("环可科技");
        info.setSoftSupport("环可科技");
        info.setMac(devicePo.getMac());
        info.setId(devicePo.getId());
        String version = devicePo.getVersion();
        JSONObject jsonObject = JSON.parseObject(version);
        if (jsonObject != null) {
            info.setHardVersion(jsonObject.getString("hardware"));
            info.setSoftVersion(jsonObject.getString("software"));
        }
        deviceDetailVo.setDeviceInfoItem(info);
    }

    public void sendGroupFunc(DeviceGroupFuncVo deviceGroupFuncVo, Integer userId, int operType) {
        List<Integer> deviceIdList = deviceGroupFuncVo.getDeviceIdList();
        String funcId = deviceGroupFuncVo.getFuncId();
        String value = deviceGroupFuncVo.getValue();
        for (Integer deviceId : deviceIdList) {
            DeviceFuncVo deviceFuncVo = new DeviceFuncVo();
            deviceFuncVo.setDeviceId(deviceId);
            deviceFuncVo.setFuncId(funcId);
            deviceFuncVo.setValue(value);
            String requestId = sendFunc(deviceFuncVo, userId, operType);
        }
    }

    public String sendFunc(DeviceFuncVo deviceFuncVo, Integer userId, Integer operType) {
        DevicePo devicePo = deviceMapper.selectById(deviceFuncVo.getDeviceId());
        if (devicePo != null) {
            Integer deviceId = devicePo.getId();
            String topic = "/down2/control/" + deviceId;
            String requestId = UUID.randomUUID().toString().replace("-", "");
            DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
            deviceOperLogPo.setFuncId(deviceFuncVo.getFuncId());
            deviceOperLogPo.setDeviceId(deviceId);
            deviceOperLogPo.setOperType(operType);
            deviceOperLogPo.setOperUserId(userId);
            deviceOperLogPo.setFuncValue(deviceFuncVo.getValue());
            deviceOperLogPo.setRequestId(requestId);
            deviceOperLogPo.setCreateTime(System.currentTimeMillis());
            deviceOperLogMapper.insert(deviceOperLogPo);
            FuncListMessage funcListMessage = new FuncListMessage();
            funcListMessage.setMsg_type("control");
            funcListMessage.setMsg_id(requestId);
            FuncItemMessage funcItemMessage = new FuncItemMessage();
            funcItemMessage.setType(deviceFuncVo.getFuncId());
            funcItemMessage.setValue(deviceFuncVo.getValue());
            funcListMessage.setDatas(Lists.newArrayList(funcItemMessage));
            mqttSendService.sendMessage(topic, JSON.toJSONString(funcListMessage));
            stringRedisTemplate.opsForHash().put("control2." + deviceId, funcItemMessage.getType(), String.valueOf(funcItemMessage.getValue()));
            return requestId;
        }
        return "";
    }


    private void getIndexData(DeviceDetailVo deviceDetailVo, Integer deviceId, Integer deviceTypeId) {

        Map<Object, Object> datas = stringRedisTemplate.opsForHash().entries("sensor2." + deviceId);
        Map<Object, Object> controlDatas = stringRedisTemplate.opsForHash().entries("control2." + deviceId);

        DeviceDetailVo.PmDataItem pm = new DeviceDetailVo.PmDataItem();
        pm.setData(getData(datas, SensorTypeEnums.PM25_IN.getCode()));
        pm.setUnit(SensorTypeEnums.PM25_IN.getUnit());
        String data = pm.getData();
        if (StringUtils.isNotEmpty(data)) {
            Integer diData = Integer.valueOf(data);
            if (diData >= 0 && diData <= 35) {
                pm.setMass("优");
            } else if (diData > 35 && diData <= 75) {
                pm.setMass("良");
            } else if (diData > 75 && diData <= 150) {
                pm.setMass("中");
            } else {
                pm.setMass("差");
            }
        } else {
            pm.setMass("");
        }
        deviceDetailVo.setPm(pm);

        DeviceDetailVo.SysDataItem co2 = new DeviceDetailVo.SysDataItem();
        co2.setData(getData(datas, SensorTypeEnums.CO2_IN.getCode()));
        co2.setUnit(SensorTypeEnums.CO2_IN.getUnit());
        deviceDetailVo.setCo2(co2);


        DeviceDetailVo.SysDataItem tvoc = new DeviceDetailVo.SysDataItem();
        tvoc.setUnit(SensorTypeEnums.TVOC_IN.getUnit());
        String tvocData = getData(datas, SensorTypeEnums.TVOC_IN.getCode());
        if (StringUtils.isNotEmpty(tvocData)) {
            Integer digData = Integer.valueOf(tvocData);
            tvoc.setData(FloatDataUtil.getFloat(digData));
        } else {
            tvoc.setData("0");
        }
        deviceDetailVo.setTvoc(tvoc);

        DeviceDetailVo.SysDataItem hcho = new DeviceDetailVo.SysDataItem();
        hcho.setUnit(SensorTypeEnums.HCHO_IN.getUnit());
        String hchoData = getData(datas, SensorTypeEnums.HCHO_IN.getCode());
        if (StringUtils.isNotEmpty(hchoData)) {
            Integer digData = Integer.valueOf(hchoData);
            hcho.setData(FloatDataUtil.getFloat(digData));
        } else {
            hcho.setData("0");
        }
        deviceDetailVo.setHcho(hcho);

        DeviceDetailVo.SysDataItem tem = new DeviceDetailVo.SysDataItem();
        tem.setData(getData(datas, SensorTypeEnums.TEMPERATURE_IN.getCode()));
        tem.setUnit(SensorTypeEnums.TEMPERATURE_IN.getUnit());
        deviceDetailVo.setTem(tem);


        DeviceDetailVo.SysDataItem hum = new DeviceDetailVo.SysDataItem();
        hum.setData(getData(datas, SensorTypeEnums.HUMIDITY_IN.getCode()));
        hum.setUnit(SensorTypeEnums.HUMIDITY_IN.getUnit());
        deviceDetailVo.setHum(hum);

        DeviceDetailVo.SysDataItem remain = new DeviceDetailVo.SysDataItem();
        remain.setData(getData(controlDatas, FuncTypeEnums.TIMER_REMAIN.getCode()));
        remain.setUnit("秒");
        deviceDetailVo.setRemain(remain);

        DeviceDetailVo.SysDataItem screen = new DeviceDetailVo.SysDataItem();
        String time = getData(controlDatas, FuncTypeEnums.TIMER_SCREEN.getCode());
        if (StringUtils.isNotEmpty(time)) {
            screen.setData(String.valueOf(unit * Integer.valueOf(time)));
        } else {
            screen.setData("0");
        }
        screen.setUnit("秒");
        deviceDetailVo.setScreen(screen);


        DeviceDetailVo.DataItem modeItem = new DeviceDetailVo.DataItem();
        modeItem.setChoice(FuncTypeEnums.MODE.getRange());
        modeItem.setType(FuncTypeEnums.MODE.getCode());
        String modeValue = getData(controlDatas, FuncTypeEnums.MODE.getCode());
        modeItem.setValue(modeValue);

        deviceDetailVo.setModeItem(modeItem);

        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(deviceTypeId);
        if (deviceTypePo != null) {
            //typeId 》abilityId
            List<DeviceAbilityPo> deviceabilityPos = deviceAbilityMapper.selectDeviceAbilitysByTypeId(deviceTypeId);
            List<String> winds = getType(FuncTypeEnums.WIND1.getCode().substring(0, 2), deviceabilityPos);
            List<DeviceDetailVo.OtherItem> dataItems = winds.stream().map(wind -> {
                DeviceDetailVo.OtherItem dataItem = new DeviceDetailVo.OtherItem();
                dataItem.setType(wind);
                StringBuilder choiceSb = new StringBuilder();
                String[] dataArray = {"一", "二", "三", "四", "五", "六", "七"};
                for (int i = 0; i < speed; i++) {
                    choiceSb.append((i + 1)).append(":").append(dataArray[i]).append("档风速");
                    if (i != (speed - 1)) {
                        choiceSb.append(",");
                    }
                }
                dataItem.setChoice(choiceSb.toString());
                dataItem.setValue(getData(controlDatas, wind));
                if (winds.size() == 1) {
                    dataItem.setName("风速");
                } else {
                    dataItem.setName(FuncTypeEnums.getByCode(wind).getMark());
                }
                return dataItem;
            }).collect(Collectors.toList());
            deviceDetailVo.setWindItems(dataItems);
            List<JSONArray> jsonArrays = Lists.newArrayList();
            List<String> uvList = getType(FuncTypeEnums.UV.getCode().substring(0, 2), deviceabilityPos);
            if (uvList.size() > 0) {
                List<DeviceDetailVo.OtherItem> uvItems = uvList.stream().map(uv -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(uv).getMark());
                    otherItem.setType(uv);
                    otherItem.setValue(getData(controlDatas, uv));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray uv = new JSONArray();
                uv.addAll(uvItems);
                jsonArrays.add(uv);
            }

            List<String> screens = getType(FuncTypeEnums.TIMER_SCREEN.getCode().substring(0, 2), deviceabilityPos);
            if (screens.size() > 0) {
                List<DeviceDetailVo.DataItem> screentItems = screens.stream().map(screenStr -> {
                    DeviceDetailVo.DataItem screen1 = new DeviceDetailVo.DataItem();
                    screen1.setValue(getData(controlDatas, screenStr));
                    screen1.setType(screenStr);
                    screen1.setChoice("0");
                    return screen1;
                }).collect(Collectors.toList());
                deviceDetailVo.setScreens(screentItems);
            }

            List<String> anoins = getType(FuncTypeEnums.ANION.getCode().substring(0, 2), deviceabilityPos);
            if (anoins.size() > 0) {
                List<DeviceDetailVo.OtherItem> anoinsItems = anoins.stream().map(anoin -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(anoin).getMark());
                    otherItem.setType(anoin);
                    otherItem.setValue(getData(controlDatas, anoin));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(anoinsItems);
                jsonArrays.add(array);
            }

            List<String> warms = getType(FuncTypeEnums.WARM.getCode().substring(0, 2), deviceabilityPos);
            if (warms.size() > 0) {
                List<DeviceDetailVo.OtherItem> warmItems = warms.stream().map(warm -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(warm).getMark());
                    otherItem.setType(warm);
                    otherItem.setValue(getData(controlDatas, warm));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(warmItems);
                jsonArrays.add(array);
            }

            List<String> humList = getType(FuncTypeEnums.HUMIDIFER.getCode().substring(0, 2), deviceabilityPos);
            if (humList.size() > 0) {
                List<DeviceDetailVo.OtherItem> humItems = humList.stream().map(humStr -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(humStr).getMark());
                    otherItem.setType(humStr);
                    otherItem.setValue(getData(controlDatas, humStr));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(humItems);
                jsonArrays.add(array);
            }

            List<String> deHumList = getType(FuncTypeEnums.DEHUMIDIFER.getCode().substring(0, 2), deviceabilityPos);
            if (deHumList.size() > 0) {
                List<DeviceDetailVo.OtherItem> dehumItems = deHumList.stream().map(dehum -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(dehum).getMark());
                    otherItem.setType(dehum);
                    otherItem.setValue(getData(controlDatas, dehum));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(dehumItems);
                jsonArrays.add(array);
            }

            List<String> valves = getType(FuncTypeEnums.VALVE1.getCode().substring(0, 2), deviceabilityPos);
            if (valves.size() > 0) {
                List<DeviceDetailVo.OtherItem> valvesItems = valves.stream().map(valve -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    if (valves.size() == 1) {
                        otherItem.setName("循环阀");
                    } else {
                        otherItem.setName(FuncTypeEnums.getByCode(valve).getMark());
                    }
                    otherItem.setType(valve);
                    otherItem.setValue(getData(controlDatas, valve));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(valvesItems);
                jsonArrays.add(array);
            }

            List<String> frankList = getType(FuncTypeEnums.FRANKLINISM.getCode().substring(0, 2), deviceabilityPos);
            if (frankList.size() > 0) {
                List<DeviceDetailVo.OtherItem> frankItems = frankList.stream().map(frank -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(frank).getMark());
                    otherItem.setType(frank);
                    otherItem.setValue(getData(controlDatas, frank));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(frankItems);
                jsonArrays.add(array);
            }

            List<String> heatList = getType(FuncTypeEnums.HEAT.getCode().substring(0, 2), deviceabilityPos);
            if (heatList.size() > 0) {
                List<DeviceDetailVo.OtherItem> heatItems = heatList.stream().map(heat -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(heat).getMark());
                    otherItem.setType(heat);
                    otherItem.setValue(getData(controlDatas, heat));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(heatItems);
                jsonArrays.add(array);
            }
            deviceDetailVo.setFuncs(jsonArrays);

            List<DeviceDetailVo.OtherItem> timers = Lists.newArrayList();
            deviceDetailVo.setTimers(timers);
            deviceDetailVo.setFuncs(jsonArrays);

            List<String> openList = getType(FuncTypeEnums.TIMER_OEPN.getCode().substring(0, 2), deviceabilityPos);
            if (openList.size() > 0) {
                String open = openList.get(0);
                DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                otherItem.setName(FuncTypeEnums.getByCode(open).getMark());
                otherItem.setType(open);
                data = getData(controlDatas, open);
                if (StringUtils.isNotEmpty(data)) {
                    otherItem.setValue(getData(controlDatas, open));
                } else {
                    otherItem.setValue("0");
                }
                timers.add(otherItem);
            }

            List<String> closeList = getType(FuncTypeEnums.TIMER_CLOSE.getCode().substring(0, 2), deviceabilityPos);
            if (closeList.size() > 0) {
                String close = closeList.get(0);
                DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                otherItem.setName(FuncTypeEnums.getByCode(close).getMark());
                otherItem.setType(close);
                data = getData(controlDatas, close);
                if (StringUtils.isNotEmpty(data)) {
                    otherItem.setValue(getData(controlDatas, close));
                } else {
                    otherItem.setValue("0");
                }
                timers.add(otherItem);
            }
        }
        DeviceDetailVo.DataItem childItem = new DeviceDetailVo.DataItem();
        childItem.setType(FuncTypeEnums.CHILD_LOCK.getCode());
        childItem.setChoice("0:未开,1:已开");
        childItem.setValue(getData(controlDatas, FuncTypeEnums.CHILD_LOCK.getCode()));
        deviceDetailVo.setChildItem(childItem);
    }

    private List<String> getType(String smallType, List<DeviceAbilityPo> deviceabilityPos) {
        List<String> retList = Lists.newArrayList();
        for (DeviceAbilityPo deviceabilityPo : deviceabilityPos) {
            if (deviceabilityPo.getDirValue().startsWith(smallType)) {
                retList.add(deviceabilityPo.getDirValue());
            }
        }
        return retList;
    }

    private String getData(Map<Object, Object> map, String key) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        }
        return "0";
    }

    private static int getAqi(Integer pm2_5) {
        float[] tbl_aqi = {0f, 50f, 100f, 150f, 200f, 300f, 400f, 500f};
        float[] tbl_pm2_5 = {0f, 35f, 75f, 115f, 150f, 250f, 350f, 500f};
        int i;
        if (pm2_5 > tbl_pm2_5[7]) {
            return (int) tbl_aqi[7];
        }
        for (i = 0; i < 8 - 1; i++) {
            if ((pm2_5 >= tbl_pm2_5[i]) && (pm2_5 < tbl_pm2_5[i + 1])) {
                break;
            }
        }
        return (int) (((tbl_aqi[i + 1] - tbl_aqi[i]) / (tbl_pm2_5[i + 1] - tbl_pm2_5[i]) * (pm2_5 - tbl_pm2_5[i]) + tbl_aqi[i]));
    }

    /*public boolean verifyUser(Integer userId, Integer deviceId) {
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        Integer hostDeviceId = devicePo.getHostDeviceId();
        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        String wxOpenId = customerUserPo.getOpenId();
        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
        deviceCustomerUserRelationPo.setOpenId(wxOpenId);
        if(hostDeviceId != null){
            deviceCustomerUserRelationPo.setDeviceId(hostDeviceId);
        }else{
            deviceCustomerUserRelationPo.setDeviceId(deviceId);
        }
        Integer count = deviceCustomerUserRelationMapper.queryRelationCount(deviceCustomerUserRelationPo);
        if (count > 0) {
            return true;
        }
        return false;
    }*/
}
