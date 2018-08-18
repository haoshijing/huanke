package com.huanke.iot.base.dao.device.ablity;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblitySetRelationPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:21
 */
public interface DeviceAblitySetRelationMapper extends BaseMapper<DeviceAblitySetRelationPo> {

    DeviceAblitySetRelationPo selectById(DeviceAblitySetRelationPo queryDeviceAblitySetRelationPo);

    List<DeviceAblitySetRelationPo> selectByAblitySetId(Integer ablitySetId);


    Integer deleteByAblitySetId(Integer ablitySetId);

    Integer deleteByAblityId(DeviceAblitySetRelationPo queryDeviceAblitySetRelationPo);

    Integer deleteRelationForJoinId(@Param("deviceId") Integer deviceId, @Param("userId") Integer userId);
}