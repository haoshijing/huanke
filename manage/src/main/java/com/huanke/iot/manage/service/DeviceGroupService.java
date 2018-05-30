package com.huanke.iot.manage.service;

import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.manage.controller.device.request.DeviceGroupQueryRequest;
import com.huanke.iot.manage.controller.device.response.DeviceGroupItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceGroupService {

    @Autowired
    private DeviceGroupMapper deviceGroupMapper;

    public List<DeviceGroupItemVo> selectList(DeviceGroupQueryRequest request) {
        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setGroupName(request.getName());
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceGroupPo> deviceGroupPos = deviceGroupMapper.selectList(deviceGroupPo,limit,offset);

        return deviceGroupPos.stream().map(deviceGroupPo1 -> {
            DeviceGroupItemVo itemVo = new DeviceGroupItemVo();
            itemVo.setGroupName(deviceGroupPo1.getGroupName());
            itemVo.setId(deviceGroupPo1.getId());
            itemVo.setIcon(deviceGroupPo1.getIcon());
            return itemVo;
        }).collect(Collectors.toList());
    }

    public Integer selectCount(DeviceGroupQueryRequest request) {
        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setGroupName(request.getName());
        return deviceGroupMapper.selectCount(deviceGroupPo);
    }
}
