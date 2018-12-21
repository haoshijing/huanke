package com.huanke.iot.manage.controller;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.project.JobService;
import com.huanke.iot.manage.vo.response.device.data.WarnDataVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @ApiOperation("查询报警数据看板")
    @GetMapping(value = "/queryWarnData")
    public ApiResponse<List<WarnDataVo>> queryWarnData(){
        List<WarnDataVo> warnDataVoList = jobService.queryWarnData();
        return new ApiResponse<>(warnDataVoList);
    }
}
