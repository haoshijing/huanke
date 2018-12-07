package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.request.project.ImplementRequest;
import com.huanke.iot.base.resp.project.ImplementRsp;
import com.huanke.iot.manage.service.project.ImplService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述:
 * 实施工程controller
 *
 * @author onlymark
 * @create 2018-11-16 上午9:33
 */
@RestController
@RequestMapping("/api/implement")
@Slf4j
public class ImplementController {

    @Autowired
    private ImplService implService;

    @ApiOperation("添加实施工程信息")
    @PostMapping(value = "/addImpl")
    public ApiResponse<Boolean> addProject(@RequestBody ImplementRequest request) {
        Boolean result = implService.addImplement(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改实施工程信息")
    @PostMapping(value = "/editImpl")
    public ApiResponse<Boolean> editImpl(@RequestBody ImplementRequest request) {
        Boolean result = implService.addImplement(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("根据工程查实施工程信息")
    @PostMapping(value = "/select/{projectId}")
    public ApiResponse<List<ImplementRsp>> selectImplements(@PathVariable("projectId") Integer projectId) {
        List<ImplementRsp> implementRspList = implService.selectImplements(projectId);
        return new ApiResponse<>(implementRspList);
    }

    @ApiOperation("查单个实施工程信息")
    @GetMapping(value = "/select/{implId}")
    public ApiResponse<ImplementRsp> select(@PathVariable("implId") Integer implId) {
        ImplementRsp implementSingleRsp = implService.select(implId);
        return new ApiResponse<>(implementSingleRsp);
    }

}
