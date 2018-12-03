package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.po.project.ProjectMaterialLog;
import com.huanke.iot.base.request.project.MateriaUpdateRequest;
import com.huanke.iot.base.resp.project.JobMateria;
import com.huanke.iot.base.resp.project.ProjectJobMateriaRsp;
import com.huanke.iot.manage.service.project.MateriaService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述:
 * controller
 *
 * @author onlymark
 * @create 2018-11-16 上午9:33
 */
@RestController
@RequestMapping("/api/materia")
@Slf4j
public class MateriaController {
    @Autowired
    private MateriaService materiaService;

    @ApiOperation("更新材料数量")
    @PostMapping(value = "/updateMateria")
    public ApiResponse<Boolean> updateMateria(@RequestBody MateriaUpdateRequest request) {
        Boolean result = materiaService.updateMateria(request, null);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查询是否存在材料")
    @GetMapping(value = "/ifExistMateria/{jobId}")
    public ApiResponse<ProjectJobMateriaRsp> ifExistMateria(@PathVariable("jobId") Integer jobId) {
        ProjectJobMateriaRsp projectJobMateriaRsp = materiaService.ifExistMateria(jobId);
        return new ApiResponse<>(projectJobMateriaRsp);
    }

    @ApiOperation("查询任务待审核状态下材料情况")
    @GetMapping(value = "/queryJobMateria/{jobId}")
    public ApiResponse<List<JobMateria>> queryJobMateria(@PathVariable("jobId") Integer jobId) {
        List<JobMateria> jobMateriaList = materiaService.queryJobMateria(jobId);
        return new ApiResponse<>(jobMateriaList);
    }

    @ApiOperation("查询任务待审核状态下材料情况")
    @GetMapping(value = "/queryJobMateriaLog/{materialId}")
    public ApiResponse<List<ProjectMaterialLog>> queryJobMateriaLog(@PathVariable("materialId") Integer materialId) {
        List<ProjectMaterialLog> projectMaterialLogList = materiaService.queryJobMateriaLog(materialId);
        return new ApiResponse<>(projectMaterialLogList);
    }


}
