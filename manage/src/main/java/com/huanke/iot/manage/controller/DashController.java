package com.huanke.iot.manage.controller;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.resp.project.MaintenanceDataVo;
import com.huanke.iot.base.resp.project.ProjectModelPercentVo;
import com.huanke.iot.manage.service.device.typeModel.DeviceModelService;
import com.huanke.iot.manage.service.project.JobService;
import com.huanke.iot.manage.vo.response.device.data.DashJobVo;
import com.huanke.iot.manage.vo.response.device.data.WarnDataVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据看板controller
 * @author haoshijing
 * @version 2018年04月02日 15:14
 **/
@RestController
@RequestMapping("/api/dashboard")
@Slf4j
public class DashController {
    @Autowired
    private JobService jobService;
    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private DeviceModelService deviceModelService;

    @ApiOperation("查询报警数据看板")
    @GetMapping(value = "/queryWarnData")
    public ApiResponse<List<WarnDataVo>> queryWarnData(){
        List<WarnDataVo> warnDataVoList = jobService.queryWarnData();
        return new ApiResponse<>(warnDataVoList);
    }

    @ApiOperation("查询维护信息看板")
    @GetMapping(value = "/queryMaintenance")
    public ApiResponse<List<MaintenanceDataVo>> queryMaintenance(){
        List<MaintenanceDataVo> maintenanceDataVoList = jobService.queryDataMaintenance();
        return new ApiResponse<>(maintenanceDataVoList);
    }

    @ApiOperation("查询任务数据看板")
    @GetMapping(value = "/queryJobDash")
    public ApiResponse<List<DashJobVo>> queryJobDash(){
        List<DashJobVo> dashJobVoList = jobService.queryJobDash();
        return new ApiResponse<>(dashJobVoList);
    }

    @ApiOperation("查询单个工程下型号比例看板")
    @GetMapping(value = "/queryModelPercent/{projectId}")
    public ApiResponse<List<ProjectModelPercentVo>> queryModelPercent(@PathVariable("projectId") Integer projectId){
        List<ProjectModelPercentVo> projectModelPercentVoList = deviceModelService.queryModelPercent(projectId);
        return new ApiResponse<>(projectModelPercentVoList);
    }
}
