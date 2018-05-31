package com.huanke.iot.manage.service;

import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.user.AppUserPo;
import com.huanke.iot.manage.controller.device.request.DeviceGroupQueryRequest;
import com.huanke.iot.manage.controller.device.response.DeviceGroupItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceGroupService {

    @Autowired
    private DeviceGroupMapper deviceGroupMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    public List<DeviceGroupItemVo> selectList(DeviceGroupQueryRequest request) {
        DeviceGroupPo queryGroup = new DeviceGroupPo();
        queryGroup.setGroupName(request.getName());
        queryGroup.setStatus(1);
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();
        List<DeviceGroupPo> deviceGroupPos = deviceGroupMapper.selectList(queryGroup,limit,offset);

        return deviceGroupPos.stream().map(deviceGroupPo -> {
            DeviceGroupItemVo itemVo = new DeviceGroupItemVo();
            itemVo.setGroupName(deviceGroupPo.getGroupName());
            itemVo.setId(deviceGroupPo.getId());
            itemVo.setIcon(deviceGroupPo.getIcon());
            DeviceGroupItemPo groupItemPo = new DeviceGroupItemPo();
            groupItemPo.setGroupId(deviceGroupPo.getId());
            groupItemPo.setIsMaster(1);

            List<DeviceGroupItemPo> itemPos = deviceGroupMapper.queryGroupItems(groupItemPo);
            if(itemPos.size() > 0){
                DeviceGroupItemPo deviceGroupItemPo = itemPos.get(0);
                Integer userId= deviceGroupItemPo.getUserId();
                AppUserPo appUserPo = appUserMapper.selectById(userId);
                if(appUserPo != null){
                    itemVo.setMaskNickname(appUserPo.getNickname());
                }
            }
            itemVo.setMemo(deviceGroupPo.getMemo());
            itemVo.setVideoCover(deviceGroupPo.getVideoCover());
            itemVo.setVideoUrl(deviceGroupPo.getVideoUrl());
            return itemVo;
        }).collect(Collectors.toList());
    }

    public Integer selectCount(DeviceGroupQueryRequest request) {
        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setGroupName(request.getName());
        deviceGroupPo.setStatus(1);
        return deviceGroupMapper.selectCount(deviceGroupPo);
    }
}
