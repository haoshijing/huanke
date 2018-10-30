package com.huanke.iot.manage.service.device.ability;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceParamsMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.dto.DeviceParamsConfigDto;
import com.huanke.iot.base.dto.DeviceParamsDto;
import com.huanke.iot.base.po.device.DeviceParamsPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsConfigVo;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsConfigVoRequest;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private DeviceModelAbilityMapper deviceModelAbilityMapper;
    @Autowired
    private DeviceParamsMapper deviceParamsMapper;

    public List<DeviceParamsVo> queryParamsAbility(Integer deviceId) {
        List<DeviceParamsVo> deviceParamsVoList = new ArrayList<>();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        Integer modelId = devicePo.getModelId();
        List<DeviceParamsDto> deviceParamsDtoList = deviceModelAbilityMapper.queryParamsAbility(modelId);
        for (DeviceParamsDto deviceParamsDto : deviceParamsDtoList) {
            DeviceParamsVo deviceParamsVo = new DeviceParamsVo();
            BeanUtils.copyProperties(deviceParamsDto, deviceParamsVo);
            deviceParamsVoList.add(deviceParamsVo);
        }
        return deviceParamsVoList;
    }

    public List<DeviceParamsConfigVo> queryParamConfig(Integer deviceId) {
        List<DeviceParamsConfigVo> deviceParamsConfigVoList = new ArrayList<>();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        Integer modelId = devicePo.getModelId();
        List<DeviceParamsConfigDto> deviceParamsConfigDtoList = deviceParamsMapper.queryParamsConfig(deviceId, modelId);
        for (DeviceParamsConfigDto deviceParamsConfigDto : deviceParamsConfigDtoList) {
            DeviceParamsConfigVo deviceParamsConfigVo = new DeviceParamsConfigVo();
            BeanUtils.copyProperties(deviceParamsConfigDto, deviceParamsConfigVo);
            List<String> valueList = Arrays.asList(deviceParamsConfigDto.getValue().split(","));
            deviceParamsConfigVo.setValuesList(valueList);
            deviceParamsConfigVoList.add(deviceParamsConfigVo);
        }
        return deviceParamsConfigVoList;
    }

    @Transactional
    public Boolean addParamConfig(DeviceParamsConfigVoRequest deviceParamsConfigVoRequest) {
        Boolean result = false;
        Integer deviceId = deviceParamsConfigVoRequest.getDeviceId();
        List<DeviceParamsConfigVo> deviceParamsConfigVoList = deviceParamsConfigVoRequest.getDeviceParamsConfigVoList();
        //删除逻辑
        List<DeviceParamsPo> deviceParamsPoList = deviceParamsMapper.findExistByDeviceId(deviceId);
        List<DeviceParamsPo> toDeleteList = new ArrayList<DeviceParamsPo>();
        for(DeviceParamsPo deviceParamsPo:deviceParamsPoList) {
            boolean flag = false;
            for(DeviceParamsConfigVo deviceParamsConfigVo:deviceParamsConfigVoList){
                if(!(deviceParamsPo.getAbilityId().equals(deviceParamsConfigVo.getAbilityId())&&deviceParamsPo.getSort().equals(deviceParamsConfigVo.getSort()))){
                    flag = true;
                }else{
                    flag = false;
                    break;
                }
            }
            if(flag){
                toDeleteList.add(deviceParamsPo);
            }
        }
        if(toDeleteList.size()>0) {
            deviceParamsMapper.deleteBatch(toDeleteList);
        }
        //添加更新逻辑
        for (DeviceParamsConfigVo deviceParamsConfigVo : deviceParamsConfigVoList) {
            DeviceParamsPo deviceParamsPo = new DeviceParamsPo();
            deviceParamsPo.setAbilityId(deviceParamsConfigVo.getAbilityId());
            deviceParamsPo.setDeviceId(deviceId);
            deviceParamsPo.setTypeName(deviceParamsConfigVo.getAbilityParamsName());
            String value = String.join(",", deviceParamsConfigVo.getValuesList());
            deviceParamsPo.setValue(value);
            deviceParamsPo.setSort(deviceParamsConfigVo.getSort());
            deviceParamsPo.setStatus(CommonConstant.STATUS_YES);
            addOrUpdateDeviceParams(deviceParamsPo);
        }
        return result;
    }

    private void addOrUpdateDeviceParams(DeviceParamsPo deviceParamsPo) {
        DeviceParamsPo exitDeviceParamsPo = deviceParamsMapper.findByDeviceIdAndAbilityId(deviceParamsPo.getDeviceId(), deviceParamsPo.getAbilityId(),deviceParamsPo.getSort());
        if(exitDeviceParamsPo == null){
            //新增
            deviceParamsPo.setCreateTime(System.currentTimeMillis());
            deviceParamsMapper.insert(deviceParamsPo);
            //添加新增人
        }else{
            //更新
            deviceParamsPo.setId(exitDeviceParamsPo.getId());
            deviceParamsPo.setUpdateWay(2);//更改途径 2-后台
            deviceParamsPo.setLastUpdateTime(System.currentTimeMillis());
            deviceParamsMapper.updateById(deviceParamsPo);
            //添加更新人
        }
    }
}
