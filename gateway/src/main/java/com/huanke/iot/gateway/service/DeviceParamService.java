package com.huanke.iot.gateway.service;

import com.huanke.iot.base.constant.DeviceParamConstants;
import com.huanke.iot.base.dao.device.DeviceParamsMapper;
import com.huanke.iot.base.po.device.DeviceParamsPo;
import com.huanke.iot.gateway.io.impl.CfgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 描述:
 * 设备传参service
 *
 * @author onlymark
 * @create 2018-10-30 下午4:14
 */
@Repository
@Slf4j
public class DeviceParamService {
    @Autowired
    private DeviceParamsMapper deviceParamsMapper;

    @Transactional
    public void updateParam(Integer deviceId, List<CfgHandler.CfgConfig> cfgConfigs) {
        for (CfgHandler.CfgConfig cfgConfig : cfgConfigs) {
            String type = cfgConfig.getType();
            String[] split = type.split(".");
            List<String> valueList = cfgConfig.getValues();
            DeviceParamsPo deviceParamsPo = deviceParamsMapper.findExistByDeviceIdAndTypeNameAndSort(deviceId, split[0], Integer.valueOf(split[1]));
            deviceParamsPo.setValue(String.join(",", valueList));
            deviceParamsPo.setLastUpdateTime(System.currentTimeMillis());
            deviceParamsPo.setUpdateWay(DeviceParamConstants.UPLOAD);
            deviceParamsMapper.updateById(deviceParamsPo);
        }
    }
}
