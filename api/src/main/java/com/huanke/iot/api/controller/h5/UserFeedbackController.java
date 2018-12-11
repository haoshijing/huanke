package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.UserFeedbackRequest;
import com.huanke.iot.api.controller.h5.req.UserRepairInfo;
import com.huanke.iot.api.controller.h5.response.RepairInfoLogVo;
import com.huanke.iot.api.controller.h5.response.RuleInfoVo;
import com.huanke.iot.api.service.user.UserFeedbackService;
import com.huanke.iot.base.api.ApiResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @ApiOperation("获取规则分类信息")
    @RequestMapping("/getRuleInfo")
    public ApiResponse<Object> getRuleInfo() {
        Integer customerId = getCurrentCustomerId();
        log.debug("获取规则分类信息，customerId={}", customerId);
        List<RuleInfoVo> resp = userFeedbackService.getRuleInfo(customerId);
        return new ApiResponse<>(resp);
    }

    @ApiOperation("反馈报修信息")
    @RequestMapping("/repairInfo")
    public ApiResponse<Object> repairInfo(@RequestBody UserRepairInfo userRepairInfo) {
        Integer userId = getCurrentUserId();
        Integer custId = getCurrentCustomerId();
        log.debug("反馈报修信息，typeId={},description={}", userRepairInfo.getRuleId(),userRepairInfo.getDescription());
        try {
            String resp = userFeedbackService.addRepairInfo(userRepairInfo, userId,custId);
            return new ApiResponse<>(resp);
        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse<>("反馈失败，请联系客服处理！");
        }
    }

    @ApiOperation("获取反馈报修历史")
    @RequestMapping("/getRepairInfoLog")
    public ApiResponse<Object> getRepairInfoLog(@RequestBody UserRepairInfo userRepairInfo) {
        Integer userId = getCurrentUserId();
        Integer custId = getCurrentCustomerId();
        log.debug("获取反馈报修历史，userId={}", userId);
        List<RepairInfoLogVo> resp = userFeedbackService.getRepairInfoLog(userId,custId,userRepairInfo.getDeviceId());
        return new ApiResponse<>(resp);
    }
}
