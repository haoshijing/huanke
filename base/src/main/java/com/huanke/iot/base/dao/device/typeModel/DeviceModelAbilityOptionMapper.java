package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceModelAbilityOptionMapper extends BaseMapper<DeviceModelAbilityOptionPo> {

    DeviceModelAbilityOptionPo getByJoinId(@Param("modelAbilityId") Integer modelAbilityId, @Param("abilityOptionId") Integer abilityOptionId);
    List<DeviceModelAbilityOptionPo> getOptionsByJoinId(@Param("modelAbilityId") Integer modelAbilityId);
}

