package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.project.JobFlowStatusRequest;
import com.huanke.iot.base.request.project.JobQueryRequest;
import com.huanke.iot.base.request.project.JobRequest;
import com.huanke.iot.base.resp.project.JobDetailRsp;
import com.huanke.iot.base.resp.project.JobRsp;
import com.huanke.iot.manage.service.project.JobService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述:
 * 任务controller
 *
 * @author onlymark
 * @create 2018-11-16 上午9:33
 */
@RestController
@RequestMapping("/api/job")
@Slf4j
public class JobController {
    @Autowired
    private JobService jobService;

    @ApiOperation("查询任务列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<JobRsp> selectList(@RequestBody JobQueryRequest request){
        JobRsp jobRsp = jobService.selectList(request);
        return new ApiResponse<>(jobRsp);
    }

    @ApiOperation("查询单个任务")
    @GetMapping(value = "/select/{jobId}")
    public ApiResponse<JobDetailRsp> selectPlan(@PathVariable("jobId") Integer jobId) {
        JobDetailRsp jobDetailRsp = jobService.selectById(jobId);
        return new ApiResponse<>(jobDetailRsp);
    }

    @ApiOperation("添加任务信息")
    @PostMapping(value = "/addJob")
    public ApiResponse<Boolean> addJob(@RequestBody JobRequest request) {
        Boolean result = jobService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    /*@ApiOperation("修改任务信息")
    @PostMapping(value = "/editPlan")
    public ApiResponse<Boolean> editPlan(@RequestBody PlanRequest request) {
        Boolean result = planService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }*/

    /*@ApiOperation("删除任务信息")
    @PostMapping(value = "/deletePlan")
    public ApiResponse<Boolean> deletePlan(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要删除设备信息");
        }
        Boolean result = planService.deletePlan(valueList);
        return new ApiResponse<>(result);
    }*/

    @ApiOperation("批量改变")
    @PostMapping(value = "/flowJob")
    public ApiResponse<Boolean> flowJob(@RequestBody JobFlowStatusRequest request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要禁用规则信息");
        }
        Boolean result = jobService.flowJob(request);
        return new ApiResponse<>(result);
    }
}
