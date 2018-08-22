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

    int updateByDeviceId(DevicePo devicePo);

    /**
     * 查设备客户Id
     * @param devicePo
     * @return
     */
    Integer getCustomerId(DevicePo devicePo);
    Integer updateByDeviceId(DevicePo devicePo);

}
