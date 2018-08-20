package com.huanke.iot.manage.controller.device.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.vo.request.device.group.DeviceGroupAddNewDeviceRequest;
import com.huanke.iot.manage.vo.request.device.group.DeviceGroupCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.group.DeviceGroupQueryRequest;
import com.huanke.iot.manage.service.device.group.DeviceGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deviceGroup")
public class DeviceGroupController {

    @Autowired
    private DeviceGroupService deviceGroupService;


    /**
     * 创建新集群并向其中添加设备，从设备列表进入，添加已经选中的设备
     *2018-08-18
     * @param deviceGroupCreateOrUpdateRequest
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addNewGroupAndDevice",method = RequestMethod.POST)
    public  ApiResponse<Boolean> addNewGroupAndDevice(@RequestBody DeviceGroupCreateOrUpdateRequest deviceGroupCreateOrUpdateRequest) throws Exception{
        //首先创建集群
        Boolean ret =  deviceGroupService.createGroup(deviceGroupCreateOrUpdateRequest);
        //集群创建成功后获取集群ID，向其中添加选中的设备
        if(ret){
            deviceGroupCreateOrUpdateRequest.setId(deviceGroupService.queryIdByName(deviceGroupCreateOrUpdateRequest).getId());
            //当有选中设备时加入选中的设备，没有选中设备时只创建新的集群
            if(null != deviceGroupCreateOrUpdateRequest.getDeviceGroupAddNewDeviceRequests()) {
                ret = deviceGroupService.addDeviceToGroup(deviceGroupCreateOrUpdateRequest);
            }
        }
        return new ApiResponse<>(ret);
    }

    /**
     *在设备列表中点击集群时，显示设备列表中已有集群的集群名称，若存在多个集群，则返回错误
     * @param deviceGroupAddNewDeviceRequest
     * @return
     * @throws Exception
     */
    @RequestMapping("/queryGroupByDevice")
    public  ApiResponse<DeviceGroupPo> queryGroupByDevice(@RequestBody DeviceGroupAddNewDeviceRequest deviceGroupAddNewDeviceRequest) throws Exception{
        DeviceGroupPo deviceGroupPo=null;
        List<DeviceGroupAddNewDeviceRequest> deviceList=deviceGroupAddNewDeviceRequest.getDeviceGroupAddNewDeviceRequests();
        if(null != deviceList) {
            if(deviceGroupService.isGroupConflict(deviceList)){
                return new ApiResponse<>(RetCode.ERROR,"设备列表中存在多个集群");
            }
            else {
                deviceGroupPo=deviceGroupService.queryGroupName(deviceList);
            }
        }
        return new ApiResponse(deviceGroupPo);
    }



//    @RequestMapping("/select")
//    public ApiResponse<List<DeviceGroupItemVo>> selectList(@RequestBody DeviceGroupQueryRequest request){
//        List<DeviceGroupItemVo> groupItemVos = deviceGroupService.selectList(request);
//        return new ApiResponse<>(groupItemVos);
//    }

//    @RequestMapping("/update")
//    public ApiResponse<Boolean> updateDeviceGroup(@RequestBody DeviceGroupUpdateVo updateVo){
//        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
//        deviceGroupPo.setId(updateVo.getId());
//        deviceGroupService.updateGroup(deviceGroupPo);
//        return new ApiResponse<>(true);
//    }
//    @RequestMapping("/selectCount")
//    public ApiResponse<Integer> selectCount(@RequestBody DeviceGroupQueryRequest request){
//        Integer count = deviceGroupService.selectCount(request);
//        return new ApiResponse<>(count);
//    }
}
