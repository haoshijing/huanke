package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.controller.h5.req.DeviceParamConfigRequest;
import com.huanke.iot.api.controller.h5.response.DeviceParamsVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceParamConstants;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceParamsMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.po.device.DeviceParamsPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 描述:
 * 设备传参service
 *
 * @author onlymark
 * @create 2018-10-30 上午10:10
 */
@Repository
@Slf4j
public class DeviceParamsService {
    @Autowired
    private DeviceParamsMapper deviceParamsMapper;
    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;
    @Autowired
    private MqttSendService mqttSendService;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;
    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelAbilityOptionMapper;

    /**
     * 判断某功能项是否有配置
     *
     * @param deviceId
     * @param typeName
     * @return
     */
    public Boolean ifExist(Integer deviceId, String typeName) {
        List<DeviceParamsPo> deviceParamsPoList = deviceParamsMapper.findExistByDeviceIdAndTypeName(deviceId, typeName);
        if (deviceParamsPoList.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 查某功能项参数配置
     *
     * @param deviceId
     * @param typeName
     * @return
     */
    public List<DeviceParamsVo> paramList(Integer deviceId, String typeName) {
        List<DeviceParamsVo> deviceParamsVoList = new ArrayList<>();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        Integer modelId = devicePo.getModelId();
        List<DeviceAbilityPo> deviceAbilityPoList = deviceAbilityMapper.queryLikeByTypeName(typeName);
        for (DeviceAbilityPo deviceAbilityPo : deviceAbilityPoList) {
            String[] split = deviceAbilityPo.getDirValue().split("\\.");
            Integer abilityId = deviceAbilityPo.getId();
            List<DeviceModelAbilityOptionPo> deviceModelAbilityOptionPoList = deviceModelAbilityOptionMapper.queryByModelIdAbilityId(modelId, abilityId);
            //型号配置项为禁用，则size是0
            if(deviceModelAbilityOptionPoList == null || deviceModelAbilityOptionPoList.size() == 0){
                continue;
            }
            DeviceParamsVo deviceParamsVo = new DeviceParamsVo();
            deviceParamsVo.setSort(Integer.valueOf(split[1]));
            deviceParamsVo.setAbilityTypeName(split[0]);
            //具体配置
            DeviceParamsPo deviceParamsPo = deviceParamsMapper.findExistByDeviceIdAndTypeNameAndSort(deviceId, split[0], Integer.valueOf(split[1]));
            List<String> valueList = new ArrayList<>();
            if(deviceParamsPo != null){
                String[] values = deviceParamsPo.getValue().split(",");
                valueList = Arrays.asList(values);
            }
            List<DeviceParamsVo.ConfigValue> configValuesList = new ArrayList<>();
            for(int i=0; i<deviceModelAbilityOptionPoList.size(); i++){
                DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionPoList.get(i);
                DeviceParamsVo.ConfigValue configValue = new DeviceParamsVo.ConfigValue();
                configValue.setDefaultValue(deviceModelAbilityOptionPo.getDefaultValue());
                configValue.setMinValue(deviceModelAbilityOptionPo.getMinVal());
                configValue.setMaxValue(deviceModelAbilityOptionPo.getMaxVal());
                configValue.setDefinedName(deviceModelAbilityOptionPo.getDefinedName());
                if(valueList.size()>0){
                    configValue.setCurrentValue(Integer.valueOf(valueList.get(i)));
                }
                configValuesList.add(configValue);
            }
            deviceParamsVo.setConfigValuesList(configValuesList);
            deviceParamsVoList.add(deviceParamsVo);
        }
        return deviceParamsVoList;
    }

    /**
     * 发送设备传参指令
     *
     * @param userId
     * @param deviceId
     * @param abilityTypeName
     * @param paramConfigList
     * @return
     */
    @Transactional
    public String sendParamFunc(Integer userId, Integer deviceId, String abilityTypeName, List<DeviceParamConfigRequest.ParamConfig> paramConfigList) {
        Map<Integer, List<String>> configMap = new HashMap<>();
        for (DeviceParamConfigRequest.ParamConfig paramConfig : paramConfigList) {
            Integer sort = paramConfig.getSort();
            List<String> valuesList = paramConfig.getValuesList();
            String values = String.join(",", valuesList);
            DeviceParamsPo deviceParamsPo = new DeviceParamsPo();
            DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectByDirValue(abilityTypeName + "." + sort);
            deviceParamsPo.setDeviceId(deviceId);
            deviceParamsPo.setAbilityId(deviceAbilityPo.getId());
            deviceParamsPo.setTypeName(abilityTypeName);
            deviceParamsPo.setStatus(CommonConstant.STATUS_YES);
            deviceParamsPo.setSort(sort);
            DeviceParamsPo oldDeviceParamsPo = deviceParamsMapper.selectList(deviceParamsPo);
            if(oldDeviceParamsPo == null){
                deviceParamsPo.setValue(values);
                deviceParamsPo.setUpdateWay(DeviceParamConstants.H5);//添加渠道：1-H5
                deviceParamsMapper.insert(deviceParamsPo);
            }else{
                oldDeviceParamsPo.setValue(values);
                oldDeviceParamsPo.setUpdateWay(DeviceParamConstants.H5);//更新渠道：1-H5
                deviceParamsMapper.updateById(oldDeviceParamsPo);
            }
            configMap.put(sort, valuesList);
        }
        //发送指令给设备
        String requestId = sendFuncToDevice(userId, deviceId, abilityTypeName, configMap, 1);
        return requestId;
    }


    private String sendFuncToDevice(Integer userId, Integer deviceId, String abilityTypeName, Map<Integer, List<String>> configMap, int operType) {
        List<ConfigFuncMessage> configFuncMessages = new ArrayList<>();
        String topic = "/down2/cfgC/" + deviceId;
        DevicePo devicePo =deviceMapper.selectById(deviceId);
        String requestId = UUID.randomUUID().toString().replace("-", "");
        DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
        deviceOperLogPo.setFuncId(abilityTypeName);
        deviceOperLogPo.setDeviceId(deviceId);
        deviceOperLogPo.setOperType(operType);
        deviceOperLogPo.setOperUserId(userId);
        deviceOperLogPo.setFuncValue(configMap.toString());
        deviceOperLogPo.setRequestId(requestId);
        deviceOperLogPo.setCreateTime(System.currentTimeMillis());
        deviceOperLogMapper.insert(deviceOperLogPo);
        //发送指令
        for (Map.Entry<Integer, List<String>> entry : configMap.entrySet()) {
            Integer sort = entry.getKey();
            List<String> values = entry.getValue();
            ConfigFuncMessage configFuncMessage = new ConfigFuncMessage();
            configFuncMessage.setType(abilityTypeName + "." + sort);
            configFuncMessage.setValue(values);
            configFuncMessages.add(configFuncMessage);
        }
        Map<String, List> req = new HashMap<String, List>();
        req.put("datas", configFuncMessages);
        mqttSendService.sendMessage(topic, JSON.toJSONString(req),devicePo.isOldDevice());
        return requestId;
    }
    public String sendOldFuncToDevice(Integer userId, Integer deviceId, String abilityTypeName, List<DeviceParamConfigRequest.ParamConfig> paramConfigList){
        List<Integer> inSpeed = new ArrayList();
        List<Integer> outSpeed = new ArrayList();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if(abilityTypeName.equals("C10")){
            for (DeviceParamConfigRequest.ParamConfig paramConfig : paramConfigList) {
                if(paramConfig.getSort() == 0){
                    paramConfig.getValuesList().stream().forEach(temp->{inSpeed.add(Integer.valueOf(temp));});
                }
                if(paramConfig.getSort() == 1){
                    paramConfig.getValuesList().stream().forEach(temp->{outSpeed.add(Integer.valueOf(temp));});
                }
            }
            if((inSpeed == null || inSpeed.size() < 1 ) && (outSpeed == null || outSpeed.size() < 1 )){
                return null;
            }
            log.info("发送老风速设置指令");
            JSONObject config = new JSONObject();
            config.put("in", inSpeed);
            config.put("out", outSpeed);
            DevicePo updatePo = new DevicePo();
            updatePo.setId(deviceId);
            updatePo.setSpeedConfig(config.toString());

            int length = inSpeed.size() * 2 + outSpeed.size() * 2;
            ByteBuf byteBuf = Unpooled.buffer(2 + length);
            byteBuf.writeShort(length);
            inSpeed.forEach(speed -> {
                byteBuf.writeShort(speed);
            });
            outSpeed.forEach(speed -> {
                byteBuf.writeShort(speed);
            });
            String topic = "/down2/cfg/" + deviceId;
            mqttSendService.sendMessage(topic, byteBuf.array(),devicePo.isOldDevice());
        }
        return null;
    }

    /**
     * 查询设备返回传参状态
     *
     * @param deviceId
     * @param typeName
     * @return
     */
    public Boolean queryDeviceBack(Integer deviceId, String typeName) {
        Boolean result = true;
        List<DeviceParamsPo> existByDeviceIdAndTypeName = deviceParamsMapper.findExistByDeviceIdAndTypeName(deviceId, typeName);
        long l = System.currentTimeMillis();
        for (DeviceParamsPo deviceParamsPo : existByDeviceIdAndTypeName) {
            result = result && deviceParamsPo.getUpdateWay() == DeviceParamConstants.UPLOAD && l - deviceParamsPo.getLastUpdateTime() < 60 * 1000;
        }
        return result;
    }


    @Data
    public static class ConfigFuncMessage {
        private String type;
        private List<String> value;
    }
}
