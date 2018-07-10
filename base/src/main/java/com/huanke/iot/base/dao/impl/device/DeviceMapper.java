package com.huanke.iot.base.dao.impl.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DevicePo;

import java.util.List;

public interface DeviceMapper extends BaseMapper<DevicePo>{

    DevicePo selectByDeviceId(String deviceId);

    /**
     * 通过mac查找相应的
     * @param mac
     * @return
     */

    DevicePo selectByMac(String mac);
    int updateByDeviceId(DevicePo devicePo);

    List<DevicePo> selectAll();

    int updateOnlyDeviceId(DevicePo devicePo);
}
