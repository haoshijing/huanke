package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;

import java.util.List;

public interface DeviceCustomerRelationMapper extends BaseMapper<DeviceCustomerRelationPo> {

    DeviceCustomerRelationPo selectByDeviceId(Integer id);

    Integer deleteDeviceById(Integer id);

    Integer insertBatch(List<DeviceCustomerRelationPo> deviceCustomerRelationPoList);

}