package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceMacPo;

public interface DeviceMacMapper extends BaseMapper<DeviceMacPo>{

    DeviceMacPo selectByMac(String mac);
}
