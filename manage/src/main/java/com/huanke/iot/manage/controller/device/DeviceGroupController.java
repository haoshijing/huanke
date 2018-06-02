package com.huanke.iot.manage.controller.device;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.manage.controller.device.request.DeviceGroupQueryRequest;
import com.huanke.iot.manage.controller.device.request.DeviceGroupUpdateVo;
import com.huanke.iot.manage.controller.device.response.DeviceGroupItemVo;
import com.huanke.iot.manage.service.DeviceGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deviceGroup")
public class DeviceGroupController {

    @Autowired
    private DeviceGroupService deviceGroupService;
    @RequestMapping("/select")
    public ApiResponse<List<DeviceGroupItemVo>> selectList(@RequestBody DeviceGroupQueryRequest request){
        List<DeviceGroupItemVo> groupItemVos = deviceGroupService.selectList(request);
        return new ApiResponse<>(groupItemVos);
    }

    @RequestMapping("/update")
    public ApiResponse<Boolean> updateDeviceGroup(@RequestBody DeviceGroupUpdateVo updateVo){
        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setId(updateVo.getId());
        deviceGroupPo.setIcon(updateVo.getIcon());
        deviceGroupPo.setMemo(updateVo.getMemo());
        deviceGroupPo.setGroupName(updateVo.getGroupName());
        deviceGroupPo.setVideoCover(updateVo.getCoverUrl());
        deviceGroupPo.setVideoUrl(updateVo.getVideoUrl());
        deviceGroupService.updateGroup(deviceGroupPo);
        return new ApiResponse<>(true);
    }
    @RequestMapping("/selectCount")
    public ApiResponse<Integer> selectCount(@RequestBody DeviceGroupQueryRequest request){
        Integer count = deviceGroupService.selectCount(request);
        return new ApiResponse<>(count);
    }
}
