package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;

import java.util.List;

public interface DeviceCustomerRelationMapper extends BaseMapper<DeviceCustomerRelationPo> {

    DeviceCustomerRelationPo selectByDeviceId(Integer id);

    DeviceCustomerRelationPo selectByDeviceMac(String mac);

    Integer deleteDeviceById(Integer id);

    List<DeviceCustomerRelationPo> selectByCustomerId(Integer customerId);

    Integer deleteBatch(List<DeviceCustomerRelationPo> deviceCustomerRelationPoList);
    Integer insertBatch(List<DeviceCustomerRelationPo> deviceCustomerRelationPoList);

}