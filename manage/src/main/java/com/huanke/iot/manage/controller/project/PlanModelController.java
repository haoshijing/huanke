package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.project.PlanQueryRequest;
import com.huanke.iot.base.request.project.PlanRequest;
import com.huanke.iot.base.resp.project.PlanInfoRsp;
import com.huanke.iot.base.resp.project.PlanRsp;
import com.huanke.iot.manage.service.project.PlanModelService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述:
 * 计划模版模版controller
 *
 * @author onlymark
 * @create 2018-11-16 上午9:33
 */
@RestController
@RequestMapping("/api/planModel")
@Slf4j
public class PlanModelController {
    @Autowired
    private PlanModelService planModelService;

    @ApiOperation("查询计划模版列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<PlanRsp> selectList(@RequestBody PlanQueryRequest request) throws Exception {
        PlanRsp planRsp = planModelService.selectList(request);
        return new ApiResponse<>(planRsp);
    }

    @ApiOperation("查询单个计划模版")
    @GetMapping(value = "/select/{planModelId}")
    public ApiResponse<PlanInfoRsp> selectPlan(@PathVariable("planModelId") Integer planModelId) {
        PlanInfoRsp planInfoRsp = planModelService.selectById(planModelId);
        return new ApiResponse<>(planInfoRsp);
    }

    @ApiOperation("添加计划模版信息")
    @PostMapping(value = "/addPlanModel")
    public ApiResponse<Boolean> addPlan(@RequestBody PlanRequest request) {
        Boolean result = planModelService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改计划模版信息")
    @PostMapping(value = "/editPlanModel")
    public ApiResponse<Boolean> editPlan(@RequestBody PlanRequest request) {
        Boolean result = planModelService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量删除计划模版信息")
    @PostMapping(value = "/deletePlanModel")
    public ApiResponse<Boolean> deletePlan(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要删除设备信息");
        }
        Boolean result = planModelService.deletePlan(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量禁用计划模版")
    @PostMapping(value = "/forbitPlanModel")
    public ApiResponse<Boolean> forbitPlan(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要禁用规则信息");
        }
        Boolean result = planModelService.forbitPlan(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量启用计划模版")
    @PostMapping(value = "/reversePlanModel")
    public ApiResponse<Boolean> reversePlan(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要启用规则信息");
        }
        Boolean result = planModelService.reversePlan(valueList);
        return new ApiResponse<>(result);
    }
}
