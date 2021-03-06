package com.huanke.iot.api.controller.app;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.controller.h5.BaseController;
import com.huanke.iot.api.controller.h5.req.BaseRequest;
import com.huanke.iot.api.controller.h5.req.OccRequest;
import com.huanke.iot.api.controller.h5.req.UpdateDeviceTeamRequest;
import com.huanke.iot.api.controller.h5.response.DeviceTeamVo;
import com.huanke.iot.api.controller.h5.team.DeviceTeamNewRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamRequest;
import com.huanke.iot.api.service.device.team.DeviceTeamService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/app/api/team")
@Slf4j
@RestController
public class AppDeviceTeamController extends BaseController {
    @Autowired
    DeviceTeamService deviceTeamService;

    @PostMapping("/createTeam")
    public Object createDeviceTeam(@RequestBody DeviceTeamNewRequest deviceTeamNewRequest) {
        Integer userId = getCurrentUserIdForApp();
        Object result = deviceTeamService.createDeviceTeam(userId, deviceTeamNewRequest);
        return result;
    }

    @PostMapping("/deleteTeam")
    public Object delDeviceTeam(@RequestBody BaseRequest<Integer> request) {
        Integer teamId = request.getValue();
        Integer userId = getCurrentUserIdForApp();
        log.info("删除组操作：userId={}, teamId={}", userId, teamId);
        Object result = deviceTeamService.deleteTeam(userId, teamId);
        return result;
    }

    @PostMapping("/addTeamDevices")
    public Object addTeamDevices(@RequestBody DeviceTeamRequest deviceTeamRequest) {
        Integer userId = getCurrentUserIdForApp();
        log.info("添加组设备:userId={}, request={}", userId, JSON.toJSON(deviceTeamRequest));
        Object result = deviceTeamService.addTeamDevices(userId, deviceTeamRequest);
        return result;
    }

    @PostMapping("/updateDeviceTeam")
    public ApiResponse<Boolean> updateDeviceTeam(@RequestBody DeviceTeamRequest deviceTeamRequest) {
        Integer userId = getCurrentUserIdForApp();
        Boolean ret = deviceTeamService.updateDeviceTeam(userId, deviceTeamRequest);
        return new ApiResponse<>(ret);
    }

    @PostMapping("/updateTeamName")
    public ApiResponse<Boolean> updateTeamName(@RequestBody UpdateDeviceTeamRequest deviceTeamRequest) {
        Integer teamId = deviceTeamRequest.getTeamId();
        String teamName = deviceTeamRequest.getTeamName();
        log.info("更改设备组名字：teamId={}, teamName={}", teamId, teamName);
        Integer userId = getCurrentUserIdForApp();
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

    @PostMapping("/list")
    public Object teamList() {
        Integer userId = getCurrentUserIdForApp();
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
    @PostMapping("/occ")
    public ApiResponse<Boolean> occ(@RequestBody OccRequest occRequest) {
        Integer teamId = occRequest.getTeamId();
        Integer openCloseStatus = occRequest.getOpenCloseStatus();
        Integer userId = getCurrentUserIdForApp();
        log.info("组开关操作，操作人={}, 操作状态={}，操作组={}", userId, openCloseStatus, teamId);
        Boolean result = deviceTeamService.occ(occRequest, userId, 2);
        return new ApiResponse<>(result);
    }
}
