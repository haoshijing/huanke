package com.huanke.iot.base.dao.impl.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceRelationPo;

/**
 * @author haoshijing
 * @version 2018年04月23日 15:04
 **/
public interface DeviceRelationMapper extends BaseMapper<DeviceRelationPo> {
    Integer updateStatus(DeviceRelationPo deviceRelationPo);
}
