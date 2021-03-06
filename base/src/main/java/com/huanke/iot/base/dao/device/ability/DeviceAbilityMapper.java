package com.huanke.iot.base.dao.device.ability;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:21
 */
public interface DeviceAbilityMapper extends BaseMapper<DeviceAbilityPo> {

    List<DeviceAbilityPo> queryAll();

    Integer deleteOptionByAbilityId(Integer AbilityId);

    Integer deleteOptionByOptionId(Integer OptionId);

    List<DeviceTypeAbilitysPo> selectAbilityListByTypeId(Integer typeId);

    List<DeviceTypeAbilitysPo> selectAbilitysByType( @Param("typeId")Integer typeId,@Param("abilityType")Integer abilityType);
//    List<DeviceTypeAbilitysPo> selectListByType(DeviceAbilityPo deviceAbilityPo);


    List<DeviceAbilityPo> selectDeviceAbilitysByTypeId(Integer typeId);

    List<String> selectAbilityCodeByDeviceId(@Param("deviceId") Integer deviceId);


    List<String> getDirValuesByDeviceTypeId(Integer deviceTypeId);

    List<DeviceAbilityPo> queryLikeByTypeName(@Param("typeName")String typeName);

    DeviceAbilityPo selectByDirValue(@Param("dirValue")String dirValue);

    List<DeviceAbilityPo> selectByModelId(@Param("modelId")Integer modelId);

    DeviceAbilityPo selectByAbilityCode(@Param("abilityCode")String abilityCode);

    void flushCache();
}
