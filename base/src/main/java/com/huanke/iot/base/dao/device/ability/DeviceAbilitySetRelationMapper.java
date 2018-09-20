package com.huanke.iot.base.dao.device.ability;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.ability.DeviceAbilitySetRelationPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:21
 */
public interface DeviceAbilitySetRelationMapper extends BaseMapper<DeviceAbilitySetRelationPo> {

    DeviceAbilitySetRelationPo selectById(DeviceAbilitySetRelationPo queryDeviceAbilitySetRelationPo);

    List<DeviceAbilitySetRelationPo> selectByAbilitySetId(Integer abilitySetId);


    Integer deleteByAbilitySetId(Integer abilitySetId);

    Integer deleteByAbilityId(DeviceAbilitySetRelationPo queryDeviceAbilitySetRelationPo);

    Integer deleteRelationForJoinId(@Param("deviceId") Integer deviceId, @Param("userId") Integer userId);
}