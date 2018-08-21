package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;

public interface DeviceCustomerRelationMapper extends BaseMapper<DeviceCustomerRelationPo> {
    DeviceCustomerRelationPo selectByDeviceId(Integer id);
}
