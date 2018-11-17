package com.huanke.iot.manage.controller.info;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.customer.UserFeedbackMapper;
import com.huanke.iot.manage.service.info.UserFeedbackInfoService;
import com.huanke.iot.manage.vo.request.customer.UserFeedbackInfoVoReq;
import com.huanke.iot.manage.vo.response.device.customer.UserFeedbackInfoVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/info")
@Slf4j
public class UserFeedbackInfoController {
    @Autowired
    private UserFeedbackInfoService userFeedbackInfoService;
    @ApiOperation("查询用户反馈信息")
    @PostMapping(value = "/selectUserFeedbackInfo")
    public ApiResponse<Object> selectUserFeedbackInfo(@RequestBody UserFeedbackInfoVoReq userFeedbackInfoVoReq){
        return new ApiResponse<>(userFeedbackInfoService.selectList(userFeedbackInfoVoReq));
    }

}
