package com.huanke.iot.manage.controller;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.controller.response.DashBoardIndexVo;
import com.huanke.iot.manage.service.DashBoardIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板controller
 * @author haoshijing
 * @version 2018年04月02日 15:14
 **/
@RestController
@RequestMapping("/api/dashboard")
public class DashController {
    @Autowired
    private DashBoardIndexService dashBoardIndexService;
    public ApiResponse<DashBoardIndexVo> obtainIndexVo(){
        DashBoardIndexVo dashBoardIndexVo = dashBoardIndexService.obtainData();
        return new ApiResponse<>(dashBoardIndexVo);
    }
}
