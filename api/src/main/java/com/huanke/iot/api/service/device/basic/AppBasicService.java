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
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Slf4j
public class AppBasicService {
    @Autowired
    private WechartUtil wechartUtil;
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
        CustomerUserPo customerUserPo = new CustomerUserPo();
        customerUserPo.setMac(iMei);
        customerUserPo.setCustomerId(customerPo.getId());
        List<CustomerUserPo> customerUserPos = customerUserMapper.selectList(customerUserPo, 1000, 0);
        if(customerUserPos != null && customerUserPos.size()>0){
            //清空对应公众号下绑定的用户关系
            customerUserPos.forEach(customerUserPoTemp ->{
                customerUserPoTemp.setMac("");
                customerUserMapper.updateById(customerUserPoTemp);
            });
        }
        log.info("重置iMei，成功，appId={}，iMei={}",appId,iMei);
        respFlag=true;
        return new ApiResponse<>(respFlag);
    }

    @Transactional
    public ApiResponse<Object> addUserAppInfo(HttpServletRequest request){
        log.info("appAddUser,appId={},iMei={}",request.getParameter("appId"),request.getParameter("iMei"));
        String appId = request.getParameter("appId");
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo == null){
            log.error("appAddUser,不存在的appId={}",appId);
            return  new ApiResponse<>(false);
        }
        UserRequestContext context = UserRequestContextHolder.get();
        if(context.getCustomerVo() == null){
            context.setCustomerVo(new UserRequestContext.CustomerVo());
        }
        context.getCustomerVo().setAppId(appId);
        context.getCustomerVo().setCustomerId(customerPo.getId());
        JSONObject resp = wechartUtil.obtainAuthAccessToken(request.getParameter("code"));
        if(resp == null || StringUtils.isEmpty(resp.get("openid"))){
            log.error("appAddUser,获取openId异常，code={}，resp={}",request.getParameter("code"),resp);
            return  new ApiResponse<>(false);
        }
        String iMei = request.getParameter("iMei");
        String openId = resp.getString("openid");
        log.info("appAddUser,openId={}",openId);
        CustomerUserPo customerUserPo = new CustomerUserPo();
        customerUserPo.setMac(iMei);
        customerUserPo.setCustomerId(customerPo.getId());
        List<CustomerUserPo> customerUserPos = customerUserMapper.selectList(customerUserPo, 1000, 0);
        while(customerUserPos != null && customerUserPos.size()>0){
            //清空公众号下现有的imei关联，以及脏数据，理论上不会进来
            customerUserPos.forEach(customerUserPoTemp ->{
                customerUserPoTemp.setMac("");
                customerUserMapper.updateById(customerUserPoTemp);
            });
            customerUserPos = customerUserMapper.selectList(customerUserPo, 1000, 0);
        }
        customerUserPo = customerUserMapper.selectByOpenId(openId);
        if(customerUserPo == null){
            log.info("appAddUser,未注册的openId={}",openId);
            return  new ApiResponse<>(false);
        }
        customerUserPo.setMac(iMei);
        customerUserMapper.updateById(customerUserPo);
        log.info("appAddUser,绑定成功，openId={}，iMei={}",openId,iMei);
        return new ApiResponse<>(true);
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
        for (DeviceTypeAbilitysPo deviceTypeabilitysPo : deviceTypeabilitysPos) {
            Integer abilityId = deviceTypeabilitysPo.getAbilityId();

            DeviceModelVo.Abilitys abilitys = new DeviceModelVo.Abilitys();
            abilitys.setAbilityId(abilityId);
            DeviceAbilityPo deviceabilityPo = deviceAbilityMapper.selectById(abilityId);
            abilitys.setAbilityName(deviceabilityPo.getAbilityName());
            DeviceModelAbilityPo deviceModelabilityPo = deviceModelabilityMapper.getByJoinId(modelId, abilityId);
            if(deviceModelabilityPo != null){
                abilitys.setDefinedName(deviceModelabilityPo.getDefinedName());
            }else{
                continue;
            }
            BeanUtils.copyProperties(deviceabilityPo, abilitys);
            List<DeviceModelVo.AbilityOption> abilityOptionList = new ArrayList<>();

            List<DeviceAbilityOptionPo> deviceabilityOptionPos = deviceabilityOptionMapper.selectOptionsByAbilityId(deviceabilityPo.getId());
            //查功能项选项及别名
            for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos) {
                DeviceModelVo.AbilityOption abilityOption = new DeviceModelVo.AbilityOption();
                abilityOption.setOptionName(deviceabilityOptionPo.getOptionName());
                abilityOption.setOptionValue(deviceabilityOptionPo.getOptionValue());
                abilityOption.setMaxVal(deviceabilityOptionPo.getMaxVal());
                abilityOption.setMinVal(deviceabilityOptionPo.getMinVal());
                DeviceModelAbilityOptionPo deviceModelabilityOptionPo = deviceModelabilityOptionMapper.getByJoinId(deviceModelabilityPo.getId(), deviceabilityOptionPo.getId());
                if(deviceModelabilityOptionPo != null){
                    abilityOption.setOptionDefinedName(deviceModelabilityOptionPo.getDefinedName());
                    abilityOption.setMaxVal(deviceModelabilityOptionPo.getMaxVal());
                    abilityOption.setMinVal(deviceModelabilityOptionPo.getMinVal());
                    abilityOption.setStatus(deviceModelabilityOptionPo.getStatus());
                }
                abilityOptionList.add(abilityOption);
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
            SensorDataVo sensorDataVo = new SensorDataVo();
            SensorTypeEnums sensorTypeEnums = SensorTypeEnums.getByCode(sensorType);
            if (sensorTypeEnums == null) {
                continue;
            }
            sensorDataVo.setName(sensorTypeEnums.getMark());
            sensorDataVo.setUnit(sensorTypeEnums.getUnit());
            sensorDataVo.setType(sensorType);
            List<String> xdata = Lists.newArrayList();
            List<String> ydata = Lists.newArrayList();
            Map<String,String> map = new LinkedHashMap<String,String>();
            for (DeviceSensorStatPo deviceSensorPo : deviceSensorPos) {
                if (deviceSensorPo.getPm() == null) {
                    continue;
                }
                String value;
                if (org.apache.commons.lang3.StringUtils.equals(sensorType, SensorTypeEnums.CO2_IN.getCode())) {
                    value = deviceSensorPo.getCo2().toString();
                } else if (org.apache.commons.lang3.StringUtils.equals(sensorType, SensorTypeEnums.HUMIDITY_IN.getCode())) {
                    value = deviceSensorPo.getHum().toString();
                } else if (org.apache.commons.lang3.StringUtils.equals(sensorType, SensorTypeEnums.TEMPERATURE_IN.getCode())) {
                    value = deviceSensorPo.getTem().toString();
                } else if (org.apache.commons.lang3.StringUtils.equals(sensorType, SensorTypeEnums.HCHO_IN.getCode())) {
                    value = FloatDataUtil.getFloat(deviceSensorPo.getHcho());
                } else if (org.apache.commons.lang3.StringUtils.equals(sensorType, SensorTypeEnums.PM25_IN.getCode())) {
                    if (deviceSensorPo.getPm() != null) {
                        value = deviceSensorPo.getPm().toString();
                    } else {
                        value = "";
                    }
                } else if (org.apache.commons.lang3.StringUtils.equals(sensorType, SensorTypeEnums.TVOC_IN.getCode())) {

                    value = FloatDataUtil.getFloat(deviceSensorPo.getTvoc());
                } else {
                    continue;
                }
                map.put(new DateTime(deviceSensorPo.getStartTime()).toString("yyyy-MM-dd HH:00:00"),value);
            }
            for(String key : map.keySet()){
                xdata.add(key);
                ydata.add(map.get(key));
            }
            sensorDataVo.setXdata(xdata);
            sensorDataVo.setYdata(ydata);
            if (!ydata.isEmpty()) {
                sensorDataVos.add(sensorDataVo);
            }
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
            if(androidConfig!=null){
                return androidConfig.getDeviceChangePassword();
            }
        }
        return "1188";
    }

    public List getCustomerSceneInfo(){
        UserRequestContext context = UserRequestContextHolder.get();
        Integer customerId = context.getCustomerVo().getCustomerId();
        AndroidConfigPo androidConfig = androidConfigMapper.selectConfigByCustomerId(customerId);
        if(androidConfig != null){
            AndroidScenePo androidScenePo = new AndroidScenePo();
            androidScenePo.setConfigId(androidConfig.getId());
            List<AndroidScenePo> androidScenePos = androidSceneMapper.selectListByConfigId(androidScenePo);
            if(androidScenePos != null && androidScenePos.size()>0){
                List appSceneVos = new ArrayList<AppSceneVo>();
                for(AndroidScenePo sceneTemp : androidScenePos){
                    AppSceneVo appSceneVo = new AppSceneVo();
                    appSceneVo.setId(sceneTemp.getId());
                    appSceneVo.setCustomerId(sceneTemp.getConfigId());
                    appSceneVo.setName(sceneTemp.getName());
                    appSceneVo.setImgsCover(sceneTemp.getImgsCover());
                    appSceneVo.setDescription(sceneTemp.getDescription());
                    appSceneVo.setStatus(sceneTemp.getStatus());
                    appSceneVo.setCreateTime(sceneTemp.getCreateTime());
                    appSceneVo.setLastUpdateTime(sceneTemp.getLastUpdateTime());

                    AndroidSceneImgPo androidSceneImgPo = new AndroidSceneImgPo();
                    androidSceneImgPo.setAndroidSceneId(sceneTemp.getId());
                    List<AndroidSceneImgPo> androidSceneImgPos = androidSceneImgMapper.selectListBySceneId(androidSceneImgPo);
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
                    appSceneVos.add(appSceneVo);
                }
                return appSceneVos;
            }
        }
        return null;
    }
}
