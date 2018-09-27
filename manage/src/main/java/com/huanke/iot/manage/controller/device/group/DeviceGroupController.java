package com.huanke.iot.manage.controller.device.group;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.group.DeviceGroupPo;
import com.huanke.iot.manage.vo.request.device.group.GroupCreateOrUpdateRequest;
import com.huanke.iot.manage.service.device.group.DeviceGroupService;
import com.huanke.iot.manage.vo.request.device.group.GroupQueryRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import com.huanke.iot.manage.vo.response.device.group.DeviceGroupDetailVo;
import com.huanke.iot.manage.vo.response.device.group.DeviceGroupListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deviceGroup")
@Slf4j
public class DeviceGroupController {

    @Autowired
    private DeviceGroupService deviceGroupService;


    /**
     * 创建新集群并向其中添加设备，从设备列表进入，添加已经选中的设备
     *2018-09-27
     * @param groupCreateOrUpdateRequest
     * @return
     * @throws Exception
     */
    @ApiOperation("创建或更新集群，向其中添加指定mac地址的设备(如果从设备列表进入，则添加已经选中的设备)")
    @RequestMapping(value = "/addOrUpdateGroupAndDevice",method = RequestMethod.POST)
    public  ApiResponse<DeviceGroupPo> addOrUpdateGroupAndDevice(@RequestBody GroupCreateOrUpdateRequest groupCreateOrUpdateRequest){
        //集群名不可为空
        if(!StringUtils.isNotEmpty(groupCreateOrUpdateRequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"集群名不可为空");
        }
        //客户id不可为空
        if(null == groupCreateOrUpdateRequest.getCustomerId()||0 == groupCreateOrUpdateRequest.getCustomerId()){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户不可为空");
        }
        //查询设备名称是否存在
        DeviceQueryRequest.DeviceQueryList device = this.deviceGroupService.queryDeviceByMac(groupCreateOrUpdateRequest.getDeviceQueryRequest());
        if(null != device){
            return new ApiResponse<>(RetCode.OK,"所选设备中mac地址 "+device.getMac()+" 不存在或已被删除");
        }
        try {
            return this.deviceGroupService.createOrUpdateGroup(groupCreateOrUpdateRequest);
        } catch (Exception e) {
            log.error("添加群失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "添加集群异常");
        }
    }

    @ApiOperation("根据查询条件分页查询设备")
    @RequestMapping(value = "/queryGroupByPage",method = RequestMethod.POST)
    public ApiResponse<List<DeviceGroupListVo>> queryGroupByPage(@RequestBody GroupQueryRequest groupQueryRequest){
        try {
            return this.deviceGroupService.queryGroupByPage(groupQueryRequest);

        }catch (Exception e){
            log.error("集群查询列表错误 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"集群查询列表异常");
        }
    }

    @ApiOperation("根据id查询集群详情")
    @RequestMapping(value = "/queryGroupById/{id}",method = RequestMethod.POST)
    public ApiResponse<DeviceGroupDetailVo> queryGroupById(@PathVariable("id") Integer groupId){
        try {
            return this.deviceGroupService.queryGroupById(groupId);
        }catch (Exception e){
            log.error("集群详情查询失败 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"集群查询异常");
        }
    }

    @ApiOperation("根据id删除集群")
    @RequestMapping(value = "/deleteGroupById/{id}",method = RequestMethod.POST)
    public ApiResponse<Boolean> deleteGroupById(@PathVariable("id") Integer groupId){
        try {
            return this.deviceGroupService.deleteOneGroup(groupId);
        }catch (Exception e){
            log.error("删除集群失败 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"删除集群异常");
        }
    }

    @ApiOperation("查询集群总数")
    @RequestMapping(value = "/queryGroupCount",method = RequestMethod.GET)
    public ApiResponse<Integer> queryGroupCount(){
        try{
            return this.deviceGroupService.queryGroupCount();
        }catch (Exception e){
            log.error("查询集群数量错误 =  {}",e);
            return new ApiResponse<>(RetCode.ERROR,"查询集群总数异常");
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
    public  ApiResponse<DeviceGroupPo> queryGroupByDevice(@RequestBody DeviceQueryRequest deviceQueryRequest){
        DeviceGroupPo deviceGroupPo=null;
        try {
            List<DeviceQueryRequest.DeviceQueryList> deviceLists=deviceQueryRequest.getDeviceList();
            if(null != deviceLists) {
                if(deviceGroupService.isGroupConflict(deviceLists)){
                    return new ApiResponse<>(RetCode.ERROR,"设备列表中存在多个集群");
                }
                else {
                    deviceGroupPo=deviceGroupService.queryGroupName(deviceLists);
                }
            }
        } catch (Exception e) {
            log.error("集群失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "集群失败");
        }
        return new ApiResponse(RetCode.OK,"查询成功",deviceGroupPo);
    }
}