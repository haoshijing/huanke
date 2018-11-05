package com.huanke.iot.base.dao.device.ability;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:21
 */
public interface DeviceAbilityOptionMapper extends BaseMapper<DeviceAbilityOptionPo> {

    List<DeviceAbilityOptionPo> selectOptionsByAbilityId(Integer abilityId);
    List<DeviceAbilityOptionPo> selectActiveOptionsByAbilityId(Integer abilityId);
}
