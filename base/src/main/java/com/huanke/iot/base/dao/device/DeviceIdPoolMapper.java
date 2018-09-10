package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceIdPoolPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceIdPoolMapper extends BaseMapper<DeviceIdPoolPo> {

    Integer insertBatch(List<DeviceIdPoolPo> deviceIdPoolPoList);

    Integer updateBatch(List<DeviceIdPoolPo> deviceIdPoolPoList);

    DeviceIdPoolPo selectByWxDeviceId(String wxDeviceId);
}
