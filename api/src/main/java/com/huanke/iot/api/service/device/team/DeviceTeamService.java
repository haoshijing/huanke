package com.huanke.iot.api.service.device.team;

import com.alibaba.druid.util.StringUtils;
import com.huanke.iot.api.controller.h5.group.DeviceGroupNewRequest;
import com.huanke.iot.api.controller.h5.group.DeviceGroupRequest;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月08日 13:22
 **/
@Repository
@Slf4j
public class DeviceTeamService {
    
    @Autowired
    DeviceTeamMapper deviceTeamMapper;

    @Autowired
    DeviceMapper deviceMapper;


    public Integer createDeviceGroup(Integer userId, DeviceGroupNewRequest newRequest) {
        String groupName = newRequest.getGroupName();
        DeviceGroupPo queryPo = new DeviceGroupPo();
        queryPo.setGroupName(groupName);
        queryPo.setUserId(userId);
        DeviceGroupPo queryGroupPo = deviceTeamMapper.queryByName(queryPo);
        if (queryGroupPo != null && queryGroupPo.getStatus() == 1) {
            return 0;
        }
        Integer groupId = 0;
        if (queryGroupPo == null) {
            DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
            deviceGroupPo.setCreateTime(System.currentTimeMillis());
            deviceGroupPo.setUserId(userId);
            deviceGroupPo.setGroupName(groupName);
            deviceGroupPo.setStatus(1);
            deviceTeamMapper.insert(deviceGroupPo);
            groupId = deviceGroupPo.getId();
        } else {
            groupId = queryGroupPo.getId();
            DeviceGroupPo updatePo = new DeviceGroupPo();
            updatePo.setGroupName(newRequest.getGroupName());
            updatePo.setStatus(1);
            updatePo.setId(groupId);
            int ret = deviceTeamMapper.updateById(updatePo);
            log.info("ret = {}",ret);
        }
        if (!CollectionUtils.isEmpty(newRequest.getDeviceIds())) {
            DeviceGroupRequest deviceGroupRequest = new DeviceGroupRequest();
            deviceGroupRequest.setDeviceIds(newRequest.getDeviceIds());
            deviceGroupRequest.setGroupId(groupId);
            updateDeviceGroup(userId, deviceGroupRequest);
            return 1;
        }
        return 0;
    }

    public Boolean deleteGroup(Integer userId, Integer groupId) {
        DeviceGroupPo groupPo = deviceTeamMapper.selectById(groupId);
        if (groupPo != null && StringUtils.equals(groupPo.getGroupName(), "默认组")) {
            return false;
        }
        Boolean updateRet = deviceTeamMapper.updateGroupStatus(userId, groupId, 2) > 0;
        if (updateRet) {
            DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
            deviceGroupPo.setGroupName("默认组");
            deviceGroupPo.setUserId(userId);
            deviceGroupPo.setStatus(1);
            List<DeviceGroupPo> deviceGroupPos = deviceTeamMapper.selectList(deviceGroupPo, 1, 0);
            Integer defaultGroupId = 0;

            if (deviceGroupPos.size() > 0) {
                defaultGroupId = deviceGroupPos.get(0).getId();
            }
            deviceTeamMapper.updateDeviceGroupItem(userId, groupId, defaultGroupId);
        }
        return updateRet;
    }

    public Boolean updateDeviceGroup(Integer userId, DeviceGroupRequest deviceGroupRequest) {
        final Integer groupId = deviceGroupRequest.getGroupId();
        deviceGroupRequest.getDeviceIds().forEach((deviceId) -> {
                    DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
                    if (devicePo != null) {
                        Integer dId = devicePo.getId();
                        deviceTeamMapper.updateDeviceGroupId(userId, groupId, dId);
                    }
                }
        );
        return true;
    }

    public Boolean updateGroupName(Integer userId, Integer groupId, String groupName) {
        DeviceGroupPo queryPo = new DeviceGroupPo();
        queryPo.setGroupName(groupName);
        queryPo.setUserId(userId);
        DeviceGroupPo deviceGroupPo = deviceTeamMapper.queryByName(queryPo);
        if (deviceGroupPo != null && deviceGroupPo.getStatus() == 1) {
            return false;
        }
        DeviceGroupPo updatePo = new DeviceGroupPo();
        updatePo.setId(groupId);
        updatePo.setGroupName(groupName);
        updatePo.setStatus(1);
        updatePo.setLastUpdateTime(System.currentTimeMillis());
        int ret = deviceTeamMapper.updateById(updatePo);
        return ret > 0;
    }
}
