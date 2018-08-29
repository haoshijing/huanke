package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DevicePo;

import java.util.List;

public interface DeviceMapper extends BaseMapper<DevicePo>{

    /**
     * 通过mac查找相应的
     * @param mac
     * @return
     */
    DevicePo selectByMac(String mac);

    /**
     * 查找所有设备
     * @return
     */
    List<DevicePo> selectAll();

    Integer deleteDevice(DevicePo devicePo);


    DevicePo selectByDeviceId(String deviceId);

    Integer updateByDeviceId(DevicePo devicePo);

    Integer insertBatch(List<DevicePo> devicePoList);

    Integer updateBatch(List<DevicePo> devicePoList);

    Integer deleteDeviceBatch(List<DevicePo> devicePoList);

    Integer getCustomerId(DevicePo devicePo);
}