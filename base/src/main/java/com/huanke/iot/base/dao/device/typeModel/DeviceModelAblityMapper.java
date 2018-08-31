package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceModelAblityMapper extends BaseMapper<DeviceModelAblityPo> {
//    DeviceTypePo selectById(DeviceTypePo deviceTypePo);

    List<DeviceModelAblityPo> selectByModelId(Integer modelId);

    void insertBatch(List<DeviceModelAblityPo> deviceModelAblityPoList);

    DeviceModelAblityPo getByJoinId(@Param("modelId") Integer modelId, @Param("abilityId") Integer abilityId);
}
