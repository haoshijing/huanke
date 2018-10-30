package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.controller.h5.req.DeviceParamConfigRequest;
import com.huanke.iot.api.controller.h5.response.DeviceParamsVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.device.DeviceParamsMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.DeviceParamsPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
        List<DeviceParamsPo> deviceParamsPoList = deviceParamsMapper.findExistByDeviceIdAndTypeName(deviceId, typeName);
        for (DeviceParamsPo deviceParamsPo : deviceParamsPoList) {
            DeviceParamsVo deviceParamsVo = new DeviceParamsVo();
            deviceParamsVo.setAbilityTypeName(deviceParamsPo.getTypeName());
            deviceParamsVo.setSort(deviceParamsPo.getSort());
            String value = deviceParamsPo.getValue();
            deviceParamsVo.setValuesList(Arrays.asList(value.split(",")));
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
            deviceParamsPo.setDeviceId(deviceId);
            deviceParamsPo.setTypeName(abilityTypeName);
            deviceParamsPo.setStatus(CommonConstant.STATUS_YES);
            deviceParamsPo.setSort(sort);
            DeviceParamsPo oldDeviceParamsPo = deviceParamsMapper.selectList(deviceParamsPo);
            oldDeviceParamsPo.setValue(values);
            oldDeviceParamsPo.setUpdateWay(1);//更新渠道：1-H5
            deviceParamsMapper.updateById(oldDeviceParamsPo);
            configMap.put(sort, valuesList);
        }
        //发送指令给设备
        String requestId = sendFuncToDevice(userId, deviceId, abilityTypeName, configMap, 1);
        return requestId;
    }

    private String sendFuncToDevice(Integer userId, Integer deviceId, String abilityTypeName, Map<Integer, List<String>> configMap, int operType) {
        List<ConfigFuncMessage> configFuncMessages = new ArrayList<>();
        String topic = "/down2/cfg/" + deviceId;
        String requestId = UUID.randomUUID().toString().replace("-", "");
        DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
        deviceOperLogPo.setFuncId(abilityTypeName);
        deviceOperLogPo.setDeviceId(deviceId);
        deviceOperLogPo.setOperType(operType);
        deviceOperLogPo.setOperUserId(userId);
        System.out.println(configMap.toString());
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
        Map<String,List> req = new HashMap<String,List>();
        req.put("datas",configFuncMessages);
        mqttSendService.sendMessage(topic, JSON.toJSONString(req));
        return requestId;
    }

    @Data
    public static class ConfigFuncMessage {
        private String type;
        private List<String> value;
    }
}
