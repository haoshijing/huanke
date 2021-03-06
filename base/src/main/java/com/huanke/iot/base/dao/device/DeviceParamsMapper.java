package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceParamsPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceParamsMapper extends BaseMapper<DeviceParamsPo> {
    List<DeviceParamsPo> queryParamsConfig(@Param("deviceId")Integer deviceId);

    DeviceParamsPo findByDeviceIdAndAbilityId(@Param("deviceId")Integer deviceId, @Param("abilityId")Integer abilityId,@Param("sort")Integer sort);

    List<DeviceParamsPo> findExistByDeviceId(@Param("deviceId")Integer deviceId);

    int deleteBatch(List<DeviceParamsPo> toDeleteList);

    List<DeviceParamsPo> findExistByDeviceIdAndTypeName(@Param("deviceId")Integer deviceId, @Param("typeName")String typeName);

    DeviceParamsPo selectList(DeviceParamsPo deviceParamsPo);

    DeviceParamsPo findExistByDeviceIdAndTypeNameAndSort(@Param("deviceId")Integer deviceId, @Param("typeName")String typeName, @Param("sort")Integer sort);
}
