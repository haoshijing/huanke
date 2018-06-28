package com.huanke.iot.base.dao.impl.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceIdPoolPo;
import org.apache.ibatis.annotations.Param;

public interface DeviceIdPoolMapper extends BaseMapper<DeviceIdPoolPo> {

    DeviceIdPoolPo selectByPublicIdAndPid(@Param("publicId") Integer publicId, @Param("wxProductId") String wxProductId);

}
