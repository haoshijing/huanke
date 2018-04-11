package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DevicePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeviceService {

    @Autowired
    DeviceGroupMapper deviceGroupMapper;

    @Autowired
    DeviceMapper deviceMapper;

    public DeviceListVo obtainMyDevice(Integer userId) {
        DeviceListVo deviceListVo = new DeviceListVo();
        DeviceGroupItemPo queryDeviceGroupItem = new DeviceGroupItemPo();
        queryDeviceGroupItem.setUserId(userId);
        queryDeviceGroupItem.setStatus(1);
        List<DeviceGroupItemPo> itemPos =  deviceGroupMapper.queryGroupItems(queryDeviceGroupItem);
        return  deviceListVo;
    }

    public boolean editDevice(String deviceId, String deviceName) {
        DevicePo devicePo = new DevicePo();
        devicePo.setDeviceId(deviceId);
        devicePo.setDeviceName(deviceName);
        return deviceMapper.updateByDeviceId(devicePo) > 0;
    }
}
