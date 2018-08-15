package com.huanke.iot.base.dao.device.data;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.data.DeviceInfoPo;

public interface DeviceInfoMapper extends BaseMapper<DeviceInfoPo> {
    DeviceInfoPo selectByDevId(String devId);

    DeviceInfoPo selectByMac(String mac);
}
