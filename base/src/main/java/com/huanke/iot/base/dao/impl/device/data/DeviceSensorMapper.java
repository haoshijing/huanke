package com.huanke.iot.base.dao.impl.device.data;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.data.DeviceAlarmPo;
import com.huanke.iot.base.po.device.data.DeviceSensorDataPo;

public interface DeviceSensorMapper extends BaseMapper<DeviceSensorDataPo> {
    DeviceSensorDataPo querySensor(Integer deviceId);
}
