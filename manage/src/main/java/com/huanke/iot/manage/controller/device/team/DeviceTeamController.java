package com.huanke.iot.manage.controller.device.team;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.manage.service.device.team.DeviceTeamService;
import com.huanke.iot.manage.vo.request.device.team.TeamCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.team.TeamListQueryRequest;
import com.huanke.iot.manage.vo.response.device.team.DeviceTeamVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@Slf4j
public class DeviceTeamController {
    @Autowired
    private DeviceTeamService deviceTeamService;
    @ApiOperation("创建新的组，并向其中添加设备")
    @RequestMapping(value = "/createNewTeam",method = RequestMethod.POST)
    public ApiResponse<Integer> createNewTeam(@RequestBody TeamCreateOrUpdateRequest teamCreateOrUpdateRequest){
        //若设备列表中无设备则仅创建新组，不添加设备
        if(null == teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList()){
            return new ApiResponse<>(this.deviceTeamService.createTeam(teamCreateOrUpdateRequest).getId());
        }
        else {
            //首先查询设备列表中是否已有设备在其他组中
            DevicePo devicePo=this.deviceTeamService.isDeviceHasTeam(teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList());
            if(null != devicePo){
                return new ApiResponse<>(RetCode.PARAM_ERROR,"设备列表中 "+devicePo.getMac()+" 地址已存在");
            }
            DeviceTeamPo deviceTeamPo=this.deviceTeamService.queryTeamByName(teamCreateOrUpdateRequest.getName());
            //若已存在该组名，则直接添加设备
            if(null != deviceTeamPo){
                //向组中添加设备
                this.deviceTeamService.addDeviceToTeam(teamCreateOrUpdateRequest,deviceTeamPo);
                //添加成功后返回teamId
                return new ApiResponse<>(RetCode.OK,"添加成功",deviceTeamPo.getId());
            }
            else {
                //首先创建组
                DeviceTeamPo resultPo=this.deviceTeamService.createTeam(teamCreateOrUpdateRequest);
                //向组中添加设备
                this.deviceTeamService.addDeviceToTeam(teamCreateOrUpdateRequest,resultPo);
                //添加成功后返回teamId
                return new ApiResponse<>(RetCode.OK,"添加成功",resultPo.getId());
            }
        }
    }

    @ApiOperation("查询组列表")
    @RequestMapping(value = "/queryTeamList",method = RequestMethod.POST)
    public ApiResponse<List<DeviceTeamVo>> queryTeamList(@RequestBody TeamListQueryRequest teamListQueryRequest){
        List<DeviceTeamVo> deviceTeamVoList=this.deviceTeamService.queryTeamList(teamListQueryRequest);
        if(0 == deviceTeamVoList.size()){
            return new ApiResponse<>(RetCode.OK,"当期设备列表中无设备",deviceTeamVoList);
        }
        else {
            return new ApiResponse<>(deviceTeamVoList);
        }
    }
}
