package com.huanke.iot.manage.service.device.ability;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceParamConstants;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceParamsMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.po.device.DeviceParamsPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityPo;
import com.huanke.iot.manage.service.gateway.MqttSendService;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsConfigVo;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsConfigVoRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceUpdateRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 描述:
 * 设备传参数service
 *
 * @author onlymark
 * @create 2018-10-29 下午2:13
 */
@Repository
@Slf4j
public class DeviceParamsService {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private DeviceModelAbilityMapper deviceModelAbilityMapper;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelAbilityOptionMapper;
    @Autowired
    private DeviceParamsMapper deviceParamsMapper;


    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;
    @Autowired
    private MqttSendService mqttSendService;

    public List<DeviceParamsConfigVo> queryAllParamConfig(Integer deviceId) {
        List<DeviceParamsConfigVo> deviceParamsConfigVoList = new ArrayList<>();
        List<DeviceParamsPo> deviceParamsPoList = deviceParamsMapper.queryParamsConfig(deviceId);
        List<DeviceModelAbilityPo> deviceModelAbilityPos = deviceModelAbilityMapper.selectByDeviceId(deviceId);
        deviceModelAbilityPos.stream().filter(temp->{return temp.getAbilityType() == 6;}).forEach(temp->{
            List<DeviceModelAbilityOptionPo> optionsByModelAbilityId = deviceModelAbilityOptionMapper.getOptionsByModelAbilityId(temp.getId());
            DeviceParamsConfigVo deviceParamsConfigVo = null;
            for (DeviceParamsPo deviceParamsPo : deviceParamsPoList) {
                if(deviceParamsPo.getAbilityId().equals(temp.getAbilityId())) {
                    deviceParamsConfigVo = new DeviceParamsConfigVo();
                    deviceParamsConfigVo.setSort(deviceParamsPo.getSort());
                    deviceParamsConfigVo.setParamName(temp.getDefinedName());
                    deviceParamsConfigVo.setAbilityTypeName(deviceParamsPo.getTypeName());
                    deviceParamsConfigVo.setConfigValuesList(new ArrayList<DeviceParamsConfigVo.ConfigValue>());
                    String[] valueList = deviceParamsPo.getValue().split(",");
                    for(int i = 0 ;i<Math.max(optionsByModelAbilityId.size(),valueList.length);i++) {
                        DeviceParamsConfigVo.ConfigValue configValue = new DeviceParamsConfigVo.ConfigValue();
                        configValue.setCurrentValue(i<valueList.length?Integer.valueOf(valueList[i]):null);
                        configValue.setDefaultValue(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getDefaultValue():null);
                        configValue.setDefinedName(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getDefinedName():null);
                        configValue.setMaxValue(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getMaxVal():null);
                        configValue.setMinValue(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getMinVal():null);
                        deviceParamsConfigVo.getConfigValuesList().add(configValue);
                    }
                    break;
                }else if(deviceParamsConfigVo == null){
                    String[] split = temp.getDirValue().split("[.]");
                    deviceParamsConfigVo = new DeviceParamsConfigVo();
                    deviceParamsConfigVo.setSort(split.length>1?Integer.valueOf(split[1]):0);
                    deviceParamsConfigVo.setParamName(temp.getDefinedName());
                    deviceParamsConfigVo.setAbilityTypeName(split.length>0?split[0]:temp.getDirValue());
                    deviceParamsConfigVo.setConfigValuesList(new ArrayList<DeviceParamsConfigVo.ConfigValue>());
                    for(int i = 0 ;i<optionsByModelAbilityId.size();i++) {
                        DeviceParamsConfigVo.ConfigValue configValue = new DeviceParamsConfigVo.ConfigValue();
                        configValue.setDefaultValue(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getDefaultValue():null);
                        configValue.setDefinedName(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getDefinedName():null);
                        configValue.setMaxValue(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getMaxVal():null);
                        configValue.setMinValue(i<optionsByModelAbilityId.size()?optionsByModelAbilityId.get(i).getMinVal():null);
                        deviceParamsConfigVo.getConfigValuesList().add(configValue);
                    }
                }
            }
            deviceParamsConfigVoList.add(deviceParamsConfigVo);
        });
        return deviceParamsConfigVoList;
    }


    @Transactional
    public Boolean addParamConfig(DeviceParamsConfigVoRequest deviceParamsConfigVoRequest) {
        Integer deviceId = deviceParamsConfigVoRequest.getDeviceId();
        String abilityTypeName = deviceParamsConfigVoRequest.getAbilityTypeName();
        List<DeviceParamsConfigVoRequest.ParamsConfig> paramConfigList = deviceParamsConfigVoRequest.getParamConfigList();
        if(deviceId == null || abilityTypeName == null || paramConfigList.size() == 0){
            log.error("参数异常：deviceId={}, abilityTypeName={}", deviceId, abilityTypeName);
            return false;
        }
        this.sendParamFunc(userService.getCurrentUser().getId(), deviceId, abilityTypeName, paramConfigList);
        this.sendOldFuncToDevice(userService.getCurrentUser().getId(), deviceId, abilityTypeName, paramConfigList);
        return true;
    }

    public String sendParamFunc(Integer userId, Integer deviceId, String abilityTypeName, List<DeviceParamsConfigVoRequest.ParamsConfig> paramConfigList) {
        Map<Integer, List<String>> configMap = new HashMap<>();
        for (DeviceParamsConfigVoRequest.ParamsConfig paramConfig : paramConfigList) {
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
                deviceParamsPo.setUpdateWay(DeviceParamConstants.MANAGE);//添加渠道：2-后管
                deviceParamsMapper.insert(deviceParamsPo);
            }else{
                oldDeviceParamsPo.setValue(values);
                oldDeviceParamsPo.setUpdateWay(DeviceParamConstants.MANAGE);//更新渠道：2-后管
                deviceParamsMapper.updateById(oldDeviceParamsPo);
            }
            configMap.put(sort, valuesList);
        }
        //发送指令给设备
        String requestId = sendFuncToDevice(userId, deviceId, abilityTypeName, configMap, DeviceParamConstants.MANAGE);
        return requestId;
    }
    private String sendFuncToDevice(Integer userId, Integer deviceId, String abilityTypeName, Map<Integer, List<String>> configMap, int operType) {
        List<DeviceUpdateRequest.ConfigFuncMessage> configFuncMessages = new ArrayList<>();
        String topic = "/down2/cfgC/" + deviceId;
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
            DeviceUpdateRequest.ConfigFuncMessage configFuncMessage = new DeviceUpdateRequest.ConfigFuncMessage();
            configFuncMessage.setType(abilityTypeName + "." + sort);
            configFuncMessage.setValue(values);
            configFuncMessages.add(configFuncMessage);
        }
        Map<String, List> req = new HashMap<String, List>();
        req.put("datas", configFuncMessages);
        mqttSendService.sendMessage(topic, JSON.toJSONString(req));
        return requestId;
    }
    public String sendOldFuncToDevice(Integer userId, Integer deviceId, String abilityTypeName, List<DeviceParamsConfigVoRequest.ParamsConfig> paramConfigList){
        List<Integer> inSpeed = new ArrayList();
        List<Integer> outSpeed = new ArrayList();
        if(abilityTypeName.equals("C10")){
            for (DeviceParamsConfigVoRequest.ParamsConfig paramConfig : paramConfigList) {
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
            net.sf.json.JSONObject config = new JSONObject();
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
            mqttSendService.sendMessage(topic, byteBuf.array());
        }
        return null;
    }
}
