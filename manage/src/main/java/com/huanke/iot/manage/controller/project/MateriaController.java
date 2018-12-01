package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.request.project.MateriaUpdateRequest;
import com.huanke.iot.manage.service.project.MateriaService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        Boolean result = materiaService.updateMateria(request);
        return new ApiResponse<>(result);
    }
/*

    @ApiOperation("查询是否存在材料")
    @GetMapping(value = "/ifExistMateria/{jobId}")
    public ApiResponse<Boolean> ifExistMateria(@PathVariable("jobId") Integer jobId) {
        Boolean result = materiaService.ifExistMateria(jobId);
        return new ApiResponse<>(result);
    }
*/


}
