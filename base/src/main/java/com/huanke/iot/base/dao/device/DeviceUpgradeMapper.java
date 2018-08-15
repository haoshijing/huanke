package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceUpgradePo;

public interface DeviceUpgradeMapper extends BaseMapper<DeviceUpgradePo> {

    DeviceUpgradePo selectByFileName(String fileName);
}
