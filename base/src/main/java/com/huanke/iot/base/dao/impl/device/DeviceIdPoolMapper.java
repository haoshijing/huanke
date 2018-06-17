package com.huanke.iot.base.dao.impl.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceIdPoolPo;

public interface DeviceIdPoolMapper extends BaseMapper<DeviceIdPoolPo> {

    DeviceIdPoolPo selectByPublicId(Integer publicId);

}
