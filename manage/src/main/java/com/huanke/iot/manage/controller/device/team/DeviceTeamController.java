package com.huanke.iot.manage.controller.device.team;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.device.team.DeviceTeamService;
import com.huanke.iot.manage.vo.request.device.team.TeamCreateOrUpdateRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device")
@Slf4j
public class DeviceTeamController {
    @Autowired
    private DeviceTeamService deviceTeamService;
    @ApiOperation("创建新的组，并向其中添加设备")
    @RequestMapping(value = "/CreateNewTeam",method = RequestMethod.POST)
    public ApiResponse<Integer> CreateNewTeam(@RequestBody TeamCreateOrUpdateRequest teamCreateOrUpdateRequest){
        return new ApiResponse<>(-1);
    }
}
