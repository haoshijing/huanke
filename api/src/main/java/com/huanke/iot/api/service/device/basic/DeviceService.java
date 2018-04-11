package com.huanke.iot.api.service.device.basic;

import com.google.common.collect.Maps;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
        final  Map<Integer,List<DeviceListVo.DeviceGroupData>> integerListMap = Maps.newHashMap();
        itemPos.forEach(deviceGroupItemPo -> {
                Integer groupId = deviceGroupItemPo.getGroupId();
                if(!integerListMap.containsKey(groupId)){
                    DeviceListVo.DeviceGroupData deviceGroupData = new DeviceListVo.DeviceGroupData();
                    DeviceGroupPo deviceGroupPo = deviceGroupMapper.selectById(groupId);
                    if(deviceGroupPo != null){
                        deviceGroupData.setGroupId(groupId);
                        deviceGroupData.setGroupName(deviceGroupPo.getGroupName());
                    }
                    DeviceListVo.DeviceItemPo deviceItemPo = new DeviceListVo.DeviceItemPo();
                }

        });
        return  deviceListVo;
    }

    public boolean editDevice(String deviceId, String deviceName) {
        DevicePo devicePo = new DevicePo();
        devicePo.setDeviceId(deviceId);
        devicePo.setDeviceName(deviceName);
        return deviceMapper.updateByDeviceId(devicePo) > 0;
    }
}
