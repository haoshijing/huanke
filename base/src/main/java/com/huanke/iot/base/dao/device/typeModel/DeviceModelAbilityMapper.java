package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.dto.DeviceParamsDto;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceModelAbilityMapper extends BaseMapper<DeviceModelAbilityPo> {
//    DeviceTypePo selectById(DeviceTypePo deviceTypePo);

    List<DeviceModelAbilityPo> selectByModelId(Integer modelId);

    void insertBatch(List<DeviceModelAbilityPo> deviceModelAbilityPoList);

    DeviceModelAbilityPo getByJoinId(@Param("modelId") Integer modelId, @Param("abilityId") Integer abilityId);

    List<DeviceAbilityPo> selectActiveByModelId(@Param("modelId") Integer modelId);

    List<DeviceParamsDto> queryParamsAbility(@Param("modelId")Integer modelId);
}
