package com.huanke.iot.manage.controller.user;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.user.UserMessageLogQueryRequest;
import com.huanke.iot.base.request.user.UserMessageQueryRequest;
import com.huanke.iot.base.request.user.UserMessageRequest;
import com.huanke.iot.base.resp.user.UserMessageLogRsp;
import com.huanke.iot.base.resp.user.UserMessageRsp;
import com.huanke.iot.manage.service.user.UserMessageService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述:
 * 用户信息controller
 *
 * @author onlymark
 * @create 2018-12-06 下午12:52
 */
@RestController
@Slf4j
@RequestMapping("/api/message")
public class UserMessageController {
    @Autowired
    private UserMessageService userMessageService;

    @ApiOperation("编辑消息")
    @PostMapping(value = "/addMessage")
    public ApiResponse<Boolean> addMessage(@RequestBody UserMessageRequest request){
        Boolean result = userMessageService.addMessage(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改消息")
    @PostMapping(value = "/editMessage")
    public ApiResponse<Boolean> editMessage(@RequestBody UserMessageRequest request) {
        Boolean result = userMessageService.editMessage(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("删除消息")
    @PostMapping(value = "/deleteMessage")
    public ApiResponse<Boolean> deleteMessage(@RequestBody BaseListRequest<Integer> request){
        Boolean result = userMessageService.delMessage(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查询消息列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<UserMessageRsp> selectList(@RequestBody UserMessageQueryRequest request) {
        UserMessageRsp userMessageRsp = userMessageService.selectList(request);
        return new ApiResponse<>(userMessageRsp);
    }

    @ApiOperation("发送消息")
    @GetMapping(value = "/sendMessage/{messageId}")
    public ApiResponse<Boolean> sendMessage(@PathVariable("messageId") Integer messageId) {
        Boolean result = userMessageService.sendMessage(messageId);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查询接收消息列表")
    @PostMapping(value = "/logSelectList")
    public ApiResponse<UserMessageLogRsp> logSelectList(@RequestBody UserMessageLogQueryRequest request) {
        UserMessageLogRsp userMessageLogRsp = userMessageService.selectMessageLogList(request);
        return new ApiResponse<>(userMessageLogRsp);
    }

    @ApiOperation("标记已读")
    @GetMapping(value = "/read/{messageLogId}")
    public ApiResponse<Boolean> read(@PathVariable("messageLogId") Integer messageLogId) {
        Boolean result = userMessageService.readMessage(messageLogId);
        return new ApiResponse<>(result);
    }

    @ApiOperation("删除日志消息")
    @PostMapping(value = "/deleteMessageLog")
    public ApiResponse<Boolean> deleteMessageLog(@RequestBody BaseListRequest<Integer> request){
        Boolean result = userMessageService.delMessageLog(request);
        return new ApiResponse<>(result);
    }

}
