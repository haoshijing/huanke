package com.huanke.iot.base.dao.device.ability;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceTypeAbilitysMapper extends BaseMapper<DeviceTypeAbilitysPo> {

    List<DeviceTypeAbilitysPo> selectByTypeId(Integer typeId);

    List<DeviceTypeAbilitysPo> selectByAbilityId(Integer abilityId);
    Integer deleteByTypeId (Integer typeId);

}