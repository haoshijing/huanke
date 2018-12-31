package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.project.MaintenanceRequest;
import com.huanke.iot.base.request.project.PlanQueryRequest;
import com.huanke.iot.base.request.project.PlanRequest;
import com.huanke.iot.base.resp.project.PlanInfoRsp;
import com.huanke.iot.base.resp.project.PlanRsp;
import com.huanke.iot.base.resp.project.PlanRspPo;
import com.huanke.iot.manage.service.project.PlanModelService;
import com.huanke.iot.manage.service.project.PlanService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 描述:
 * 计划controller
 *
 * @author onlymark
 * @create 2018-11-16 上午9:33
 */
@RestController
@RequestMapping("/api/plan")
@Slf4j
public class PlanController {
    @Autowired
    private PlanService planService;
    @Autowired
    private PlanModelService planModelService;

    @ApiOperation("查询计划列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<PlanRsp> selectList(@RequestBody PlanQueryRequest request) throws Exception {
        PlanRsp planRsp = planService.selectList(request);
        return new ApiResponse<>(planRsp);
    }

    @ApiOperation("查询单个计划")
    @GetMapping(value = "/select/{planId}")
    public ApiResponse<PlanInfoRsp> selectPlan(@PathVariable("planId") Integer planId) {
        PlanInfoRsp planInfoRsp = planService.selectById(planId);
        return new ApiResponse<>(planInfoRsp);
    }

    @ApiOperation("添加计划信息")
    @PostMapping(value = "/addPlan")
    public ApiResponse<Boolean> addPlan(@RequestBody PlanRequest request) {
        Boolean result = planService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改计划信息")
    @PostMapping(value = "/editPlan")
    public ApiResponse<Boolean> editPlan(@RequestBody PlanRequest request) {
        Boolean result = planService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("删除计划信息")
    @PostMapping(value = "/deletePlan")
    public ApiResponse<Boolean> deletePlan(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要删除设备信息");
        }
        Boolean result = planService.deletePlan(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量禁用计划")
    @PostMapping(value = "/forbitPlan")
    public ApiResponse<Boolean> forbitPlan(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要禁用规则信息");
        }
        Boolean result = planService.forbitPlan(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量启用计划")
    @PostMapping(value = "/reversePlan")
    public ApiResponse<Boolean> reversePlan(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要启用规则信息");
        }
        Boolean result = planService.reversePlan(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查工程维保")
    @PostMapping(value = "/maintenance")
    public ApiResponse<PlanRsp> maintenance(@RequestBody MaintenanceRequest request) {
        PlanRsp planRsp = planService.maintenance(request);
        return new ApiResponse<>(planRsp);
    }

    @ApiOperation("导出工程维保列表")
    @GetMapping(value = "/exportMaintenance/{projectId}")
    public ApiResponse<Boolean> exportMaintenance(@PathVariable("projectId") Integer projectId, HttpServletResponse response) throws Exception {
        Boolean result = planService.exportMaintenance(projectId, response);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查工程维保（计划）模版")
    @GetMapping(value = "/queryPlanModels")
    public ApiResponse<List<PlanRspPo>> queryPlanModels() {
        List<PlanRspPo> planRspList = planModelService.queryPlanModels();
        return new ApiResponse<>(planRspList);
    }
}
