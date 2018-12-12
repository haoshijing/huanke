package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.app.response.AppInfoVo;
import com.huanke.iot.api.controller.app.response.AppSceneVo;
import com.huanke.iot.api.controller.h5.response.DeviceModelVo;
import com.huanke.iot.api.controller.h5.response.SensorDataVo;
import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.api.util.FloatDataUtil;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.customer.*;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.ability.DeviceTypeAbilitysMapper;
import com.huanke.iot.base.dao.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.customer.*;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Slf4j
public class AppBasicService {
    @Autowired
    private WechartUtil wechartUtil;
    @Autowired
    AndroidUserInfoMapper androidUserInfoMapper;
    @Autowired
    private CustomerUserMapper customerUserMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;
    @Autowired
    private DeviceAbilityOptionMapper deviceabilityOptionMapper;
    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;
    @Autowired
    private DeviceModelAbilityMapper deviceModelabilityMapper;
    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelabilityOptionMapper;
    @Autowired
    private DeviceTypeAbilitysMapper deviceTypeabilitysMapper;
    @Autowired
    private AndroidConfigMapper androidConfigMapper;
    @Autowired
    private AndroidSceneMapper androidSceneMapper;
    @Autowired
    private AndroidSceneImgMapper androidSceneImgMapper;

    List<String> needValues = new ArrayList<>(Arrays.asList(
            SensorTypeEnums.TEMPERATURE_IN.getCode(),
            SensorTypeEnums.HUMIDITY_IN.getCode(),
            SensorTypeEnums.PM25_IN.getCode(),
            SensorTypeEnums.CO2_IN.getCode(),
            SensorTypeEnums.HCHO_IN.getCode(),
            SensorTypeEnums.TVOC_IN.getCode(),
            SensorTypeEnums.NH3_IN.getCode(),
            SensorTypeEnums.ANION_IN.getCode()));
    @Transactional
    public ApiResponse<Object> removeIMeiInfo(HttpServletRequest request){
        String appId = request.getParameter("appId");
        String iMei = request.getParameter("iMei");
        log.info("重置iMei，appId={}，iMei={}",appId,iMei);
        boolean respFlag = false;
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo == null){
            log.error("重置iMei，没有该公众号appId={}",appId);
            return new ApiResponse<>(respFlag);
        }
        AndroidUserInfoPo androidUserInfoPo = androidUserInfoMapper.selectByCustomerAndImei(customerPo.getId(), iMei);
        if(androidUserInfoPo!=null&&androidUserInfoPo.getCustUserId()!=null){
            androidUserInfoPo.setCustUserId(null);
            androidUserInfoMapper.updateById(androidUserInfoPo);
        }
        log.info("重置iMei，成功，appId={}，iMei={}",appId,iMei);
        respFlag=true;
        return new ApiResponse<>(respFlag);
    }

    @Transactional
    public String addUserAppInfo(HttpServletRequest request){
        log.info("appAddUser,appId={},iMei={}",request.getParameter("appId"),request.getParameter("iMei"));
        String appId = request.getParameter("appId");
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo == null){
            log.error("appAddUser,不存在的appId={}",appId);
            return "APP错误，请安装正确的APP！";
        }
        UserRequestContext context = UserRequestContextHolder.get();
        if(context.getCustomerVo() == null){
            context.setCustomerVo(new UserRequestContext.CustomerVo());
        }
        context.getCustomerVo().setAppId(appId);
        context.getCustomerVo().setCustomerId(customerPo.getId());
        JSONObject resp = wechartUtil.obtainAuthAccessToken(request.getParameter("code"));
        if(resp == null || StringUtils.isEmpty(resp.get("openid").toString())){
            log.error("appAddUser,获取openId异常，code={}，resp={}",request.getParameter("code"),resp);
            return "微信错误，请重新扫码！";
        }
        String iMei = request.getParameter("iMei");
        String openId = resp.getString("openid");
        log.info("appAddUser,openId={}",openId);
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(openId);
        if(customerUserPo == null){
            log.info("appAddUser,未注册的openId={}",openId);
            return "请关注公众号！";
        }
        AndroidUserInfoPo androidUserInfoPo = new AndroidUserInfoPo();
        androidUserInfoPo.setImei(iMei);
        androidUserInfoPo.setCustomerId(customerPo.getId());
        androidUserInfoPo = androidUserInfoMapper.selectByCustomerAndImei(customerPo.getId(),iMei);
        if(androidUserInfoPo != null ){
            androidUserInfoPo.setCustUserId(customerUserPo.getId());
            androidUserInfoPo.setUpdateTime(System.currentTimeMillis());
            androidUserInfoMapper.updateById(androidUserInfoPo);
        }else{
            androidUserInfoPo = new AndroidUserInfoPo();
            androidUserInfoPo.setCustomerId(customerPo.getId());
            androidUserInfoPo.setImei(iMei);
            androidUserInfoPo .setCustUserId(customerUserPo.getId());
            androidUserInfoPo .setUpdateTime(System.currentTimeMillis());
            androidUserInfoMapper.insert(androidUserInfoPo);
        }
        log.info("appAddUser,绑定成功，openId={}，iMei={}",openId,iMei);
        return "绑定成功！";
    }

    public Object getQRCode(String appId){
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo != null){
            AndroidConfigPo androidConfigPo = androidConfigMapper.selectConfigByCustomerId(customerPo.getId());
            if(androidConfigPo != null){
                return androidConfigPo.getQrcode();
            }
        }
        return "";
    }
    public DeviceModelVo getModelVo(Integer deviceId) {
        DeviceModelVo deviceModelVo = new DeviceModelVo();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        Integer modelId = devicePo.getModelId();
        DeviceModelPo deviceModelPo = deviceModelMapper.selectById(modelId);
        Integer typeId = deviceModelPo.getTypeId();
        Integer formatId = deviceModelPo.getFormatId();
        deviceModelVo.setFormatId(formatId);
        deviceModelVo.setModelId(modelId);
        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(devicePo.getTypeId());
        deviceModelVo.setTypeNo(deviceTypePo.getTypeNo());
        //查型号硬件功能项
        List<DeviceModelVo.Abilitys> abilitysList = new ArrayList<>();
        List<DeviceTypeAbilitysPo> deviceTypeabilitysPos = deviceTypeabilitysMapper.selectByTypeId(typeId);
        //缓存功能项集
        List<DeviceAbilityPo> deviceAbilityPoCaches = deviceAbilityMapper.selectList(new DeviceAbilityPo(), 10000, 0);
        Map<Integer,DeviceAbilityPo> deviceAbilityPoMap = deviceAbilityPoCaches.stream().collect(Collectors.toMap(DeviceAbilityPo::getId, a -> a,(k1, k2)->k1));
        List<DeviceModelAbilityPo> deviceModelAbilityPoCaches = deviceModelabilityMapper.selectByModelId(modelId);
        Map<Integer, DeviceModelAbilityPo> deviceModelAbilityPoMap = deviceModelAbilityPoCaches.stream().collect(Collectors.toMap(DeviceModelAbilityPo::getAbilityId, a -> a, (k1, k2) -> k1));
        for (DeviceTypeAbilitysPo deviceTypeabilitysPo : deviceTypeabilitysPos) {
            Integer abilityId = deviceTypeabilitysPo.getAbilityId();
            DeviceModelVo.Abilitys abilitys = new DeviceModelVo.Abilitys();
            abilitys.setAbilityId(abilityId);
            DeviceAbilityPo deviceabilityPo = deviceAbilityPoMap.get(abilityId);
            abilitys.setAbilityName(deviceabilityPo.getAbilityName());
            DeviceModelAbilityPo deviceModelabilityPo = deviceModelAbilityPoMap.get(abilityId);
            if(deviceModelabilityPo != null){
                abilitys.setDefinedName(deviceModelabilityPo.getDefinedName());
            }else{
                continue;
            }
            BeanUtils.copyProperties(deviceabilityPo, abilitys);
            List<DeviceModelVo.AbilityOption> abilityOptionList = new ArrayList<>();

            List<DeviceAbilityOptionPo> deviceabilityOptionPos = deviceabilityOptionMapper.selectOptionsByAbilityId(deviceabilityPo.getId());
            //查功能项选项及别名
            //缓存
            List<DeviceModelAbilityOptionPo> deviceModelAbilityOptionPoCaches = deviceModelabilityOptionMapper.getOptionsByModelAbilityId(deviceModelabilityPo.getId());
            Map<Integer, DeviceModelAbilityOptionPo> deviceModelAbilityOptionPoMap = deviceModelAbilityOptionPoCaches.stream().collect(Collectors.toMap(DeviceModelAbilityOptionPo::getAbilityOptionId, a -> a, (k1, k2) -> k1));
            for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos) {
                DeviceModelAbilityOptionPo deviceModelabilityOptionPo = deviceModelAbilityOptionPoMap.get(deviceabilityOptionPo.getId());
                if(deviceModelabilityOptionPo != null){
                    DeviceModelVo.AbilityOption abilityOption = new DeviceModelVo.AbilityOption();
                    abilityOption.setOptionName(deviceabilityOptionPo.getOptionName());
                    abilityOption.setOptionValue(deviceabilityOptionPo.getOptionValue());
                    abilityOption.setMaxVal(deviceabilityOptionPo.getMaxVal());
                    abilityOption.setMinVal(deviceabilityOptionPo.getMinVal());
                    abilityOption.setOptionDefinedName(deviceModelabilityOptionPo.getDefinedName());
                    abilityOption.setMaxVal(deviceModelabilityOptionPo.getMaxVal());
                    abilityOption.setMinVal(deviceModelabilityOptionPo.getMinVal());
                    abilityOption.setStatus(deviceModelabilityOptionPo.getStatus());
                    abilityOptionList.add(abilityOption);
                }
            }
            abilitys.setAbilityOptionList(abilityOptionList);
            abilitysList.add(abilitys);
        }
        deviceModelVo.setAbilitysList(abilitysList);
        return deviceModelVo;
    }
    public List<SensorDataVo> getHistoryData(Integer deviceId, Integer type) {
        Long startTimestamp = new DateTime().plusDays(-1).getMillis();
        Long endTimeStamp = System.currentTimeMillis();
        List<SensorDataVo> sensorDataVos = Lists.newArrayList();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo == null) {
            return null;
        }
        Integer deviceModelId = devicePo.getModelId();
        List<DeviceAbilityPo> deviceAbilityPos = deviceModelabilityMapper.selectActiveByModelId(deviceModelId);
        List<String> dirValues = deviceAbilityPos.stream().map(deviceAbilityPo -> deviceAbilityPo.getDirValue()).collect(Collectors.toList());
        List<DeviceSensorStatPo> deviceSensorPos = deviceSensorStatMapper.selectData(devicePo.getId(), startTimestamp, endTimeStamp);
        for (String sensorType : dirValues) {
            if(!needValues.contains(sensorType))continue;
            SensorDataVo sensorDataVo = new SensorDataVo();
            SensorTypeEnums sensorTypeEnums = SensorTypeEnums.getByCode(sensorType);
            sensorDataVo.setName(sensorTypeEnums.getMark());
            sensorDataVo.setUnit(sensorTypeEnums.getUnit());
            sensorDataVo.setType(sensorType);
            Map<String,String> map = new LinkedHashMap<>();
            for (DeviceSensorStatPo deviceSensorPo : deviceSensorPos) {
                Float value;
                switch (sensorTypeEnums){
                    case CO2_IN:
                        value = Float.valueOf(deviceSensorPo.getCo2()==null?0:deviceSensorPo.getCo2());
                        break;
                    case HUMIDITY_IN:
                        value = Float.valueOf(deviceSensorPo.getHum()==null?0:deviceSensorPo.getHum());
                        break;
                    case TEMPERATURE_IN:
                        value = Float.valueOf(deviceSensorPo.getTem()==null?0:deviceSensorPo.getTem());
                        break;
                    case HCHO_IN:
                        value = Float.valueOf((deviceSensorPo.getHcho()==null?0:deviceSensorPo.getHcho())/100);
                        break;
                    case PM25_IN:
                        value = Float.valueOf(deviceSensorPo.getPm()==null?0:deviceSensorPo.getPm());
                        break;
                    case TVOC_IN:
                        value = Float.valueOf((deviceSensorPo.getTvoc()==null?0:deviceSensorPo.getTvoc())/100);
                        break;
                    case NH3_IN:
                        value = Float.valueOf(deviceSensorPo.getNh3()==null?0:deviceSensorPo.getNh3());
                        break;
                    case ANION_IN:
                        value = Float.valueOf(deviceSensorPo.getAnion()==null?0:deviceSensorPo.getAnion());
                        break;
                        default:
                            value = Float.valueOf(0);
                }
                String key = new DateTime(deviceSensorPo.getStartTime()).toString("yyyy-MM-dd HH:00:00");
                if(map.get(key)==null){
                    map.put(key,new DecimalFormat("0.00").format(value));
                }else{
                    map.put(key,new DecimalFormat("0.00").format(Float.valueOf(map.get(key))/2+Float.valueOf(value)/2));
                }
            }
            sensorDataVo.setXdata(new ArrayList<>(map.keySet()));
            sensorDataVo.setYdata(new ArrayList<>(map.values()));
            sensorDataVos.add(sensorDataVo);
        }
        return sensorDataVos;
    }

    public AppInfoVo getApkInfo(String appId){
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo != null){
            AndroidConfigPo androidConfig = androidConfigMapper.selectConfigByCustomerId(customerPo.getId());
            if(androidConfig!=null){
                AppInfoVo appInfoVo = new AppInfoVo();
                appInfoVo.setVersionName(androidConfig.getName());
                appInfoVo.setApkUrl(androidConfig.getAppUrl());
                appInfoVo.setVersionCode(androidConfig.getVersion());
                return appInfoVo;
            }
        }
        return new AppInfoVo();
    }
    public String getPassword(String appId){
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo != null){
            AndroidConfigPo androidConfig = androidConfigMapper.selectConfigByCustomerId(customerPo.getId());
            if(androidConfig!=null && StringUtils.isNotEmpty(androidConfig.getDeviceChangePassword())){
                return androidConfig.getDeviceChangePassword();
            }
        }
        return "1188";
    }

    public AppSceneVo getCustomerSceneInfo(){
        UserRequestContext context = UserRequestContextHolder.get();
        Integer customerId = context.getCustomerVo().getCustomerId();
        AndroidConfigPo androidConfig = androidConfigMapper.selectConfigByCustomerId(customerId);
        if(androidConfig != null){
            AndroidScenePo androidScenePo = androidSceneMapper.selectByConfigId(androidConfig.getId());
            if(androidScenePo != null){
                AppSceneVo appSceneVo = new AppSceneVo();
                appSceneVo.setId(androidScenePo.getId());
                appSceneVo.setCustomerId(androidScenePo.getConfigId());
                appSceneVo.setName(androidScenePo.getName());
                appSceneVo.setImgsCover(androidScenePo.getImgsCover());
                appSceneVo.setDescription(androidScenePo.getDescription());
                appSceneVo.setStatus(androidScenePo.getStatus());
                appSceneVo.setCreateTime(androidScenePo.getCreateTime());
                appSceneVo.setLastUpdateTime(androidScenePo.getLastUpdateTime());

                AndroidSceneImgPo androidSceneImgPo = new AndroidSceneImgPo();
                androidSceneImgPo.setAndroidSceneId(androidScenePo.getId());
                List<AndroidSceneImgPo> androidSceneImgPos = androidSceneImgMapper.selectListBySceneId(androidScenePo.getId());
                if(androidSceneImgPos != null && androidSceneImgPos.size()>0){
                    List androidSceneImgs = new ArrayList<AppSceneVo.AndroidSceneImgVo>();
                    for(AndroidSceneImgPo androidSceneImgPoTemp : androidSceneImgPos) {
                        AppSceneVo.AndroidSceneImgVo androidSceneImgVo = new AppSceneVo.AndroidSceneImgVo();
                        androidSceneImgVo.setId(androidSceneImgPoTemp.getId());
                        androidSceneImgVo.setAndroidSceneId(androidSceneImgPoTemp.getAndroidSceneId());
                        androidSceneImgVo.setCreateTime(androidSceneImgPoTemp.getCreateTime());
                        androidSceneImgVo.setCustomerId(androidSceneImgPoTemp.getCustomerId());
                        androidSceneImgVo.setDescription(androidSceneImgPoTemp.getDescription());
                        androidSceneImgVo.setImgVideo(androidSceneImgPoTemp.getImgVideo());
                        androidSceneImgVo.setLastUpdateTime(androidSceneImgPoTemp.getLastUpdateTime());
                        androidSceneImgVo.setName(androidSceneImgPoTemp.getName());
                        androidSceneImgVo.setStatus(androidSceneImgPoTemp.getStatus());

                        androidSceneImgs.add(androidSceneImgVo);
                    }
                    appSceneVo.setAndroidSceneImgs(androidSceneImgs);
                };
                return appSceneVo;
            }
        }
        return null;
    }
}
