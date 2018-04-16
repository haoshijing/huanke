package com.huanke.iot.api.service.device.basic;

import com.google.common.collect.Maps;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.DeviceTypeMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.DeviceTypePo;
import com.huanke.iot.base.po.device.data.DeviceSensorDataPo;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DeviceService {

    @Autowired
    DeviceGroupMapper deviceGroupMapper;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    DeviceSensorMapper deviceSensorMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;


    public DeviceListVo obtainMyDevice(Integer userId) {
        DeviceListVo deviceListVo = new DeviceListVo();

        DeviceGroupPo queryDevicePo = new DeviceGroupPo();
        queryDevicePo.setUserId(userId);

        List<DeviceGroupPo> deviceGroupPos = deviceGroupMapper.selectList(queryDevicePo,0,100000);

        List<DeviceListVo.DeviceGroupData> groupDatas = deviceGroupPos.stream().map(
                deviceGroupPo -> {
                    DeviceListVo.DeviceGroupData deviceGroupData = new DeviceListVo.DeviceGroupData();
                    deviceGroupData.setGroupName(deviceGroupPo.getGroupName());
                    deviceGroupData.setGroupId(deviceGroupPo.getId());
                    DeviceGroupItemPo queryDeviceGroupItem = new DeviceGroupItemPo();
                    queryDeviceGroupItem.setUserId(userId);
                    queryDeviceGroupItem.setStatus(1);
                    List<DeviceGroupItemPo> itemPos = deviceGroupMapper.queryGroupItems(queryDeviceGroupItem);
                    List<DeviceListVo.DeviceItemPo> deviceItemPos = itemPos.stream().map(deviceGroupItemPo -> {
                        DeviceSensorDataPo deviceSensorDataPo = deviceSensorMapper.querySensor(deviceGroupItemPo.getDeviceId());
                        DeviceListVo.DeviceItemPo deviceItemPo = new DeviceListVo.DeviceItemPo();
                        if (deviceSensorDataPo != null) {
                            deviceItemPo.setPm(String.valueOf(deviceSensorDataPo.getPm2_5()));
                        }
                        DevicePo devicePo = deviceMapper.selectById(deviceGroupItemPo.getDeviceId());
                        deviceItemPo.setDeviceId(devicePo.getDeviceId());
                        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(devicePo.getDeviceTypeId());
                        deviceItemPo.setOnlineStatus(1);
                        deviceItemPo.setDeviceName(devicePo.getName() == null ? "默认名称" : devicePo.getName());
                        if (deviceTypePo != null) {
                            deviceItemPo.setDeviceTypeName(deviceTypePo.getName());
                            deviceItemPo.setIcon(deviceTypePo.getIcon());
                        }
                        return deviceItemPo;
                    }).collect(Collectors.toList());
                    deviceGroupData.setDeviceItemPos(deviceItemPos);
                    return deviceGroupData;
                }
        ).collect(Collectors.toList());

        deviceListVo.setGroupDataList(groupDatas);
        return deviceListVo;
    }

    public boolean editDevice(Integer userId,String deviceId, String deviceName) {
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if(devicePo == null){
            return false;
        }
        DeviceGroupItemPo deviceGroupItemPo = new DeviceGroupItemPo();
        deviceGroupItemPo.setUserId(userId);
        deviceGroupItemPo.setStatus(1);
        deviceGroupItemPo.setDeviceId(devicePo.getId());
        Integer count = deviceGroupMapper.queryItemCount(deviceGroupItemPo);
        if(count == null || count == 0){
            return  false;
        }
        DevicePo updatePo = new DevicePo();
        updatePo.setDeviceId(deviceId);
        updatePo.setName(deviceName);
        return deviceMapper.updateByDeviceId(updatePo) > 0;
    }
}
