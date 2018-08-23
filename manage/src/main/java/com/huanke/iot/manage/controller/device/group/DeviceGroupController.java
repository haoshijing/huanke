package com.huanke.iot.manage.controller.device.group;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.device.group.DeviceGroupPo;
import com.huanke.iot.manage.vo.request.device.group.DeviceGroupCreateOrUpdateRequest;
import com.huanke.iot.manage.service.device.group.DeviceGroupService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.IntegerSyntax;
import java.util.List;

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
    @ApiOperation("创建新集群并向其中添加设备，从设备列表进入，添加已经选中的设备")
    @RequestMapping(value = "/addNewGroupAndDevice",method = RequestMethod.POST)
    public  ApiResponse<Boolean> addNewGroupAndDevice(@RequestBody DeviceGroupCreateOrUpdateRequest deviceGroupCreateOrUpdateRequest) throws Exception{
        //集群名不可为空
        if(!StringUtils.isNotEmpty(deviceGroupCreateOrUpdateRequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"集群名不可为空");
        }
        //客户id不可为空
        if(null == deviceGroupCreateOrUpdateRequest.getCustomerId()||0 == deviceGroupCreateOrUpdateRequest.getCustomerId()){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户不可为空");
        }
        //若集群列表中已存在当前名称集群,则直接添加设备
        if(null != deviceGroupService.queryIdByName(deviceGroupCreateOrUpdateRequest)){
            //查询已存在集群的相关信息
            DeviceGroupPo deviceGroupPo= deviceGroupService.queryIdByName(deviceGroupCreateOrUpdateRequest);
            //当有选中设备时加入选中的设备，没有选中设备时只创建新的集群或什么也不做
            if(null != deviceGroupCreateOrUpdateRequest.getDeviceQueryRequest()) {
                deviceGroupService.addDeviceToGroup(deviceGroupCreateOrUpdateRequest,deviceGroupPo);
            }
            return new ApiResponse<>(true);
        }
        else {
            //首先创建集群,并返回新增集群的相关信息
            DeviceGroupPo deviceGroupPo= deviceGroupService.createGroup(deviceGroupCreateOrUpdateRequest);
            //集群创建成功后获取集群ID，向其中添加选中的设备

            //当有选中设备时加入选中的设备，没有选中设备时只创建新的集群
            if(null != deviceGroupCreateOrUpdateRequest.getDeviceQueryRequest()) {
                deviceGroupService.addDeviceToGroup(deviceGroupCreateOrUpdateRequest,deviceGroupPo);
            }
            return new ApiResponse<>(true);
        }
    }

    /**
     *在设备列表中点击集群时，显示设备列表中已有集群的集群名称，若存在多个集群，则返回错误
     * @param deviceQueryRequest
     * @return
     * @throws Exception
     */
    @ApiOperation("在设备列表中点击集群时，显示设备列表中已有集群的集群名称，若存在多个集群，则返回错误")
    @RequestMapping(value = "/queryGroupByDevice",method = RequestMethod.POST)
    public  ApiResponse<DeviceGroupPo> queryGroupByDevice(@RequestBody DeviceQueryRequest deviceQueryRequest) throws Exception{
        DeviceGroupPo deviceGroupPo=null;
        List<DeviceQueryRequest.DeviceQueryList> deviceLists=deviceQueryRequest.getDeviceList();
        if(null != deviceLists) {
            if(deviceGroupService.isGroupConflict(deviceLists)){
                return new ApiResponse<>(RetCode.ERROR,"设备列表中存在多个集群");
            }
            else {
                deviceGroupPo=deviceGroupService.queryGroupName(deviceLists);
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
