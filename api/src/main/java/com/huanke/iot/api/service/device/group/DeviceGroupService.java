package com.huanke.iot.api.service.device.group;

import com.huanke.iot.api.controller.h5.group.DeviceNewGroupRequest;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haoshijing
 * @version 2018年04月08日 13:22
 **/
//@Repository
public class DeviceGroupService {

    @Autowired
    DeviceGroupMapper deviceGroupMapper;

    @Autowired
    DeviceMapper deviceMapper;

    public Integer addNewDeviceGroup(DeviceNewGroupRequest deviceNewGroupRequest) {
        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setCreateTime(System.currentTimeMillis());
        deviceGroupPo.setLastUpdateTime(System.currentTimeMillis());
        deviceGroupPo.setGroupName(deviceNewGroupRequest.getGroupName());
        deviceGroupPo.setOpenId(deviceNewGroupRequest.getOpenId());
        deviceGroupMapper.insert(deviceGroupPo);

        final  Integer deviceGroupId = deviceGroupPo.getId();

        List<DeviceGroupItemPo> deviceGroupItemPoList = deviceNewGroupRequest.getDeviceIds().stream().map(deviceKey->{
            DeviceGroupItemPo deviceGroupItemPo = new DeviceGroupItemPo();
            DevicePo devicePo = deviceMapper.selectByDeviceId(deviceKey);
            deviceGroupItemPo.setDeviceId(devicePo.getId());
            deviceGroupItemPo.setGroupId(deviceGroupId);
            deviceGroupItemPo.setCreateTime(System.currentTimeMillis());
            return deviceGroupItemPo;
        }).collect(Collectors.toList());

        deviceGroupMapper.bactchInsertGroupItem(deviceGroupItemPoList);
        return deviceGroupId;
    }
}
