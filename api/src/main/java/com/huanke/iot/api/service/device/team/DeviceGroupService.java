package com.huanke.iot.api.service.device.team;

import com.huanke.iot.api.controller.h5.response.DeviceGroupVo;
import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.group.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.group.DeviceGroupPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author onlymark
 * @version 2018年08月24日
 **/
@Repository
@Slf4j
public class DeviceGroupService {
    @Autowired
    private DeviceGroupMapper deviceGroupMapper;
    @Autowired
    private DeviceGroupItemMapper deviceGroupItemMapper;
    @Autowired
    private DeviceMapper deviceMapper;

    public List<DeviceGroupVo> getGroupListByUserId(Integer userId, Integer customerId) {
        List<DeviceGroupVo> deviceGroupVoList = new ArrayList<>();
        List<DeviceGroupPo> deviceGroupPoListAll = deviceGroupMapper.selectByCustomerId(customerId);
        List<DeviceGroupPo> deviceGroupPoList = deviceGroupPoListAll.stream().filter(deviceGroupPo -> {
            if (deviceGroupPo.getMasterUserId() == userId || Arrays.asList(deviceGroupPo.getManageUserIds().split(",")).contains(String.valueOf(userId))) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        for (DeviceGroupPo deviceGroupPo : deviceGroupPoList) {
            Integer groupId = deviceGroupPo.getId();
            DeviceGroupVo deviceGroupVo = new DeviceGroupVo();
            deviceGroupVo.setId(groupId);
            deviceGroupVo.setName(deviceGroupPo.getName());
            List<DeviceGroupItemPo> deviceGroupItemPoList = deviceGroupItemMapper.selectByGroupId(groupId);

            List<DeviceGroupVo.DeviceGroupItem> deviceGroupItemList = new ArrayList<>();
            for (DeviceGroupItemPo deviceGroupItemPo : deviceGroupItemPoList) {
                Integer deviceId = deviceGroupItemPo.getDeviceId();
                DevicePo devicePo = deviceMapper.selectById(deviceId);
                DeviceGroupVo.DeviceGroupItem deviceGroupItem = new DeviceGroupVo.DeviceGroupItem();
                deviceGroupItem.setDeviceId(devicePo.getId());
                deviceGroupItem.setWxDeviceId(devicePo.getWxDeviceId());
                deviceGroupItem.setDeviceName(devicePo.getName());
                deviceGroupItemList.add(deviceGroupItem);
            }
            deviceGroupVo.setDeviceGroupItemList(deviceGroupItemList);
            deviceGroupVoList.add(deviceGroupVo);
        }
        return deviceGroupVoList;
    }
}
