package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.UserFeedbackRequest;
import com.huanke.iot.api.service.user.UserFeedbackService;
import com.huanke.iot.base.api.ApiResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/h5/api/userFeedback")
@RestController
@Slf4j
public class UserFeedbackController extends BaseController {

    @Autowired
    private UserFeedbackService userFeedbackService;


    @ApiOperation("反馈自定义信息")
    @RequestMapping("/customMessage")
    public ApiResponse<Object> customMessage(@RequestBody UserFeedbackRequest userFeedbackRequest) {
        Integer userId = getCurrentUserId();
        log.info("用户反馈信息，userId={},info={}", userId, userFeedbackRequest.getFeedbackInfo());
        String resp = userFeedbackService.saveUserFeedback(userId, userFeedbackRequest);
        return new ApiResponse<>(resp);
    }
}
