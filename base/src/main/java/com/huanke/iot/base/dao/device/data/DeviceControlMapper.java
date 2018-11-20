package com.huanke.iot.base.dao.device.data;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.data.DeviceControlData;
import org.apache.ibatis.annotations.Param;

public interface DeviceControlMapper extends BaseMapper<DeviceControlData> {
    void clearData(@Param("lastTime") long millis);
}
