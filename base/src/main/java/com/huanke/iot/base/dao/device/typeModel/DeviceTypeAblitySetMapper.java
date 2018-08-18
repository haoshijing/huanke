package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceTypeAblitySetPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceTypeAblitySetMapper extends BaseMapper<DeviceTypeAblitySetPo> {

    DeviceTypeAblitySetPo selectByTypeId(Integer typeId);

}
