package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.UpdateDeviceTeamRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamNewRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamRequest;
import com.huanke.iot.api.service.device.team.DeviceTeamService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResponse<Integer> createDeviceTeam(@RequestBody DeviceTeamNewRequest deviceTeamNewRequest) {
        Integer userId = getCurrentUserId();
        Integer teamId = deviceTeamService.createDeviceTeam(userId, deviceTeamNewRequest);
        return new ApiResponse<>(teamId);
    }

    @RequestMapping("/deleteTeam")
    public ApiResponse<Boolean> delDeviceTeam(Integer teamId) {
        Integer userId = getCurrentUserId();
        Boolean ret = deviceTeamService.deleteTeam(userId, teamId);
        return new ApiResponse<>(ret);
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
}
