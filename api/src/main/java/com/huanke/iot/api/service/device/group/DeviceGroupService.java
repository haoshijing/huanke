package com.huanke.iot.api.service.device.group;

import com.huanke.iot.api.controller.h5.group.DeviceGroupNewRequest;
import com.huanke.iot.api.controller.h5.group.DeviceGroupRequest;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haoshijing
 * @version 2018年04月08日 13:22
 **/
@Repository
public class DeviceGroupService {

    @Autowired
    DeviceGroupMapper deviceGroupMapper;

    @Autowired
    DeviceMapper deviceMapper;


    public Integer createDeviceGroup(Integer userId, DeviceGroupNewRequest newRequest) {
        String groupName = newRequest.getGroupName();
        Integer groupCount = deviceGroupMapper.queryGroupCount(userId, groupName);
        if (groupCount == null || groupCount == 0) {
            DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
            deviceGroupPo.setCreateTime(System.currentTimeMillis());
            deviceGroupPo.setUserId(userId);
            deviceGroupPo.setGroupName(groupName);
            deviceGroupPo.setStatus(1);
            deviceGroupMapper.insert(deviceGroupPo);
            Integer groupId =  deviceGroupPo.getId();
            if(!CollectionUtils.isEmpty(newRequest.getDeviceIds())) {
                DeviceGroupRequest deviceGroupRequest = new DeviceGroupRequest();
                deviceGroupRequest.setDeviceIds(newRequest.getDeviceIds());
                deviceGroupRequest.setGroupId(groupId);
                updateDeviceGroup(userId, deviceGroupRequest);
            }
            return 1;
        }
        return 0;
    }

    public Boolean deleteGroup(Integer userId, Integer groupId) {
        Boolean updateRet = deviceGroupMapper.updateGroupStatus(userId, groupId) > 0;
        if (updateRet) {
            deviceGroupMapper.updateDeviceGroupItem(userId, groupId, 0);
        }
        return updateRet;
    }

    public Boolean updateDeviceGroup(Integer userId, DeviceGroupRequest deviceGroupRequest) {
        final Integer groupId = deviceGroupRequest.getGroupId();
        deviceGroupRequest.getDeviceIds().forEach((deviceId) -> {
                    DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
                    if (devicePo != null) {
                        Integer dId = devicePo.getId();
                       deviceGroupMapper.updateDeviceGroupId(userId,groupId,dId);
                    }
                }
        );
        return true;
    }

    public Boolean updateGroupName(Integer groupId, String groupName) {
        DeviceGroupPo deviceGroupPo = deviceGroupMapper.queryByName(groupName);
        if(deviceGroupPo != null){
            return false;
        }

        DeviceGroupPo updatePo = new DeviceGroupPo();
        updatePo.setId(groupId);
        updatePo.setGroupName(groupName);
        int ret = deviceGroupMapper.updateById(updatePo);
        return ret >0;
    }
}
