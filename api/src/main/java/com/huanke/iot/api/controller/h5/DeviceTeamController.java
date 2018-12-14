package com.huanke.iot.api.controller.h5;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.controller.h5.req.BaseRequest;
import com.huanke.iot.api.controller.h5.req.OccRequest;
import com.huanke.iot.api.controller.h5.req.TeamTrusteeRequest;
import com.huanke.iot.api.controller.h5.req.UpdateDeviceTeamRequest;
import com.huanke.iot.api.controller.h5.response.DeviceTeamVo;
import com.huanke.iot.api.controller.h5.team.DeviceTeamNewRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamRequest;
import com.huanke.iot.api.service.device.team.DeviceTeamService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月22日
 **/
@RestController
@RequestMapping("/h5/api/team")
@Slf4j
public class DeviceTeamController extends BaseController {

    @Autowired
    DeviceTeamService deviceTeamService;

    @RequestMapping("/createTeam")
    public Object createDeviceTeam(@RequestBody DeviceTeamNewRequest deviceTeamNewRequest) {
        Integer userId = getCurrentUserId();
        Object result = deviceTeamService.createDeviceTeam(userId, deviceTeamNewRequest);
        return result;
    }

    @RequestMapping("/deleteTeam")
    public Object delDeviceTeam(@RequestBody BaseRequest<Integer> request) {
        Integer teamId = request.getValue();
        Integer userId = getCurrentUserId();
        log.info("删除组操作：userId={}, teamId={}", userId, teamId);
        Object result = deviceTeamService.deleteTeam(userId, teamId);
        return result;
    }

    @RequestMapping("/addTeamDevices")
    public Object addTeamDevices(@RequestBody DeviceTeamRequest deviceTeamRequest) {
        Integer userId = getCurrentUserId();
        log.info("添加组设备:userId={}, request={}", userId, JSON.toJSON(deviceTeamRequest));
        Object result = deviceTeamService.addTeamDevices(userId, deviceTeamRequest);
        return result;
    }

    @RequestMapping("/updateDeviceTeam")
    public ApiResponse<Boolean> updateDeviceTeam(@RequestBody DeviceTeamRequest deviceTeamRequest) {
        Integer userId = getCurrentUserId();
        Boolean ret = deviceTeamService.updateDeviceTeam(userId, deviceTeamRequest);
        return new ApiResponse<>(ret);
    }

    @RequestMapping("/updateTeamName")
    public ApiResponse<Boolean> updateTeamName(@RequestBody UpdateDeviceTeamRequest deviceTeamRequest) {
        Integer teamId = deviceTeamRequest.getTeamId();
        String teamName = deviceTeamRequest.getTeamName();
        log.info("更改设备组名字：teamId={}, teamName={}", teamId, teamName);
        Integer userId = getCurrentUserId();
        if (teamId == null || StringUtils.isEmpty(teamName)) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "参数错误");
        }


        ApiResponse apiResponse = new ApiResponse(true);
        Boolean ret = deviceTeamService.updateTeamName(userId, teamId, teamName);
        apiResponse.setData(ret);
        if (!ret) {
            apiResponse.setMsg("该组名已存在");
        }
        return apiResponse;
    }

    @RequestMapping("/list")
    public Object teamList() {
        Integer userId = getCurrentUserId();
        List<DeviceTeamPo> deviceTeamPoList = deviceTeamService.selectByUserId(userId);
        List<DeviceTeamVo> deviceTeamVoList = new ArrayList<>();
        for (DeviceTeamPo deviceTeamPo : deviceTeamPoList) {
            DeviceTeamVo deviceTeamVo = new DeviceTeamVo();
            deviceTeamVo.setId(deviceTeamPo.getId());
            deviceTeamVo.setName(deviceTeamPo.getName());
            deviceTeamVo.setIcon(deviceTeamPo.getIcon());
            deviceTeamVoList.add(deviceTeamVo);
        }
        return deviceTeamVoList;
    }

    /**
     * 组开/关 occ = openCloseControl
     * @return
     */
    @RequestMapping("/occ")
    public ApiResponse<Boolean> occ(@RequestBody OccRequest occRequest) {
        Integer teamId = occRequest.getTeamId();
        Integer openCloseStatus = occRequest.getOpenCloseStatus();
        Integer userId = getCurrentUserId();
        log.info("组开关操作，操作人={}, 操作状态={}，操作组={}", userId, openCloseStatus, teamId);
        Boolean result = deviceTeamService.occ(occRequest, userId, 1);
        return new ApiResponse<>(result);
    }

    @ApiOperation("通过指定用户openId托管组给另一用户")
    @RequestMapping(value = "/trusteeTeam",method = RequestMethod.POST)
    public ApiResponse<Integer> trusteeTeam(@RequestBody TeamTrusteeRequest teamTrusteeRequest){
        //首先查询要托管的用户是否存在
        try {

            CustomerUserPo queryCustomerUserPo = this.deviceTeamService.queryCustomerUser(teamTrusteeRequest.getOpenId());
            if (queryCustomerUserPo==null){
                return new ApiResponse<>(RetCode.PARAM_ERROR,"当前用户 "+teamTrusteeRequest.getOpenId()+" 不存在");
            }
            CustomerUserPo customerUserPo=this.deviceTeamService.trusteeTeam(teamTrusteeRequest);
            if(null != customerUserPo){
                return new ApiResponse<>(RetCode.OK,"托管成功",customerUserPo.getId());
            }
            else {
                return new ApiResponse<>(RetCode.ERROR,"托管失败");
            }
        } catch (Exception e) {
            log.error("托管组失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "托管组失败");
        }
    }
}
