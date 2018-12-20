package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.project.CopyProjectPlanReq;
import com.huanke.iot.base.request.project.ExistProjectNoRequest;
import com.huanke.iot.base.request.project.ProjectQueryRequest;
import com.huanke.iot.base.request.project.ProjectRequest;
import com.huanke.iot.base.resp.project.*;
import com.huanke.iot.manage.service.project.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述:
 * 计划controller
 *
 * @author onlymark
 * @create 2018-11-16 上午9:33
 */
@RestController
@RequestMapping("/api/project")
@Slf4j
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @ApiOperation("查询工程列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<ProjectRsp> selectList(@RequestBody ProjectQueryRequest request) throws Exception {
        ProjectRsp projectRsp = projectService.selectList(request);
        return new ApiResponse<>(projectRsp);
    }

    @ApiOperation("查询单个工程")
    @GetMapping(value = "/select/{projectId}")
    public ApiResponse<ProjectRequest> selectProject(@PathVariable("projectId") Integer projectId) {
        ProjectRequest projectRequest = projectService.selectById(projectId);
        return new ApiResponse<>(projectRequest);
    }

    @ApiOperation("查询单个工程设备项目信息")
    @PostMapping(value = "/selectGroups")
    public ApiResponse<List<ProjectGroupsRsp>> selectGroups(@RequestBody BaseListRequest<Integer> request) {
        List<ProjectGroupsRsp> projectGroupsRsps = projectService.selectGroups(request);
        return new ApiResponse<>(projectGroupsRsps);
    }

    @ApiOperation("添加工程信息")
    @PostMapping(value = "/addProject")
    public ApiResponse<Boolean> addProject(@RequestBody ProjectRequest request) {
        Boolean result = projectService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改计划信息")
    @PostMapping(value = "/editProject")
    public ApiResponse<Boolean> editPlan(@RequestBody ProjectRequest request) {
        Boolean result = projectService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("启用工程信息")
    @PostMapping(value = "/reverseProject")
    public ApiResponse<Boolean> reverseProject(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要启用设备信息");
        }
        Boolean result = projectService.reverseProject(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("删除工程信息")
    @PostMapping(value = "/deleteProject")
    public ApiResponse<Boolean> deleteProject(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要删除设备信息");
        }
        Boolean result = projectService.deleteProject(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查工程字典")
    @GetMapping(value = "/selectProjectDict")
    public ApiResponse<List<ProjectDictRsp>> selectProjectDict() {
        List<ProjectDictRsp> projectDictRspList = projectService.selectProjectDict();
        return new ApiResponse<>(projectDictRspList);
    }

    @ApiOperation("查工程编号是否重复")
    @PostMapping(value = "/existProjectNo")
    public ApiResponse<Boolean> existProjectNo(@RequestBody ExistProjectNoRequest request) {
        String value = request.getValue();
        if(value.isEmpty()){
            throw new BusinessException("参数错误");
        }
        Boolean result = projectService.existProjectNo(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("工程分析")
    @GetMapping(value = "/projectAnalysis/{projectId}")
    public ApiResponse<ProjectAnalysisRsp> projectAnalysis(@PathVariable("projectId") Integer projectId) {

        ProjectAnalysisRsp projectAnalysisRsp = projectService.projectAnalysis(projectId);
        return new ApiResponse<>(projectAnalysisRsp);
    }

    @ApiOperation("获取可复制工程维保")
    @GetMapping(value = "/getCopyProjectPlan/{projectId}")
    public ApiResponse<List<ProjectPlanCount>> getCopyProjectPlan(@PathVariable("projectId") Integer projectId) {

        List<ProjectPlanCount> projectPlanCounts= projectService.getCopyProjectPlan(projectId);
        return new ApiResponse<>(projectPlanCounts);
    }

    @ApiOperation("从另一个工程复制周期的维保至本工程")
    @PostMapping(value = "/copyProjectPlan")
    public ApiResponse<Boolean> copyProjectPlan(@RequestBody CopyProjectPlanReq copyProjectPlanReq) {
        Boolean resp= projectService.copyProjectPlan(copyProjectPlanReq);
        return new ApiResponse<>(resp);
    }
}
