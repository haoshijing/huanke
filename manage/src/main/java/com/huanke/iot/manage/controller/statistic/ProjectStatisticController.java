package com.huanke.iot.manage.controller.statistic;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.resp.project.JobRspPo;
import com.huanke.iot.base.resp.project.ProjectRspPo;
import com.huanke.iot.manage.service.statistic.ProjectStatisticService;
import com.huanke.iot.manage.service.statistic.StatisticService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/project/statistics")
@Slf4j
public class ProjectStatisticController {


    @Autowired
    private ProjectStatisticService projectStatisticService;

    @ApiOperation("工程区域统计")
    @GetMapping(value = "/projectLocationCount")
    public ApiResponse<List<ProjectRspPo.ProjectPercent>> projectLocationCount(){
        return this.projectStatisticService.queryProjectLocationCount();
    }

    @ApiOperation("工程量趋势统计")
    @GetMapping(value = "/projectTrendCount")
    public ApiResponse<List<ProjectRspPo.ProjectCountVo>> projectTrendCount(){
        return new ApiResponse<>(this.projectStatisticService.queryProjectTrendCount());
    }

    @ApiOperation("任务告警来源统计")
    @GetMapping(value = "/jobWarningSourceCount")
    public ApiResponse<List<JobRspPo.JobCountVo>> jobWarningSourceCount(){
        return new ApiResponse<>(this.projectStatisticService.jobWarningSourceCount());
    }

    @ApiOperation("工程设备开关机分布")
    @GetMapping(value = "/projectDevicePowerStatusCount/{projectId}")
    public ApiResponse<List<ProjectRspPo.ProjectCountVo>> projectDevicePowerStatusCount(@PathVariable("projectId") Integer projectId){
        return new ApiResponse<>(this.projectStatisticService.projectDevicePowerStatusCount(projectId));
    }
}
