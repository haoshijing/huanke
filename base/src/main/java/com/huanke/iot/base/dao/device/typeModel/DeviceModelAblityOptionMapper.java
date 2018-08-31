package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityOptionPo;
import org.apache.ibatis.annotations.Param;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceModelAblityOptionMapper extends BaseMapper<DeviceModelAblityOptionPo> {

    DeviceModelAblityOptionPo getByJoinId(@Param("modelAblityId") Integer modelAblityId, @Param("ablityOptionId") Integer ablityOptionId);
}

