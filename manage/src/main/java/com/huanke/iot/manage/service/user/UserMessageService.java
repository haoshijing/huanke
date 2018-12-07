package com.huanke.iot.manage.service.user;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.UserMessageConstant;
import com.huanke.iot.base.dao.user.UserManagerMapper;
import com.huanke.iot.base.dao.user.UserMessageLogMapper;
import com.huanke.iot.base.dao.user.UserMessageMapper;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.po.user.UserMessage;
import com.huanke.iot.base.po.user.UserMessageLog;
import com.huanke.iot.base.po.user.UserPo;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.user.UserMessageLogQueryRequest;
import com.huanke.iot.base.request.user.UserMessageQueryRequest;
import com.huanke.iot.base.request.user.UserMessageRequest;
import com.huanke.iot.base.resp.user.UserMessageLogRsp;
import com.huanke.iot.base.resp.user.UserMessageRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 用户消息service
 *
 * @author onlymark
 * @create 2018-12-06 下午12:59
 */
@Slf4j
@Repository
public class UserMessageService {
    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserManagerMapper userManagerMapper;
    @Autowired
    private UserMessageLogMapper userMessageLogMapper;

    public Boolean addMessage(UserMessageRequest request) {
        User user = userService.getCurrentUser();
        UserMessage userMessage = new UserMessage();
        BeanUtils.copyProperties(request, userMessage);
        userMessage.setStatus(CommonConstant.STATUS_YES);
        userMessage.setCreateTime(new Date());
        userMessage.setCreateUser(user.getId());
        return  userMessageMapper.insert(userMessage) > 0;
    }

    public Boolean delMessage(BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = userMessageMapper.batchDelete(userId, valueList);
        return result;
    }

    public Boolean editMessage(UserMessageRequest request) {
        User user = userService.getCurrentUser();
        UserMessage userMessage = userMessageMapper.selectById(request.getId());
        BeanUtils.copyProperties(request, userMessage);
        userMessage.setUpdateTime(new Date());
        userMessage.setUpdateUser(user.getId());
        return  userMessageMapper.updateById(userMessage) > 0;
    }

    public UserMessageRsp selectList(UserMessageQueryRequest request) {
        UserMessageRsp userMessageRsp = new UserMessageRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;
        UserMessage userMessage = new UserMessage();
        BeanUtils.copyProperties(request, userMessage);
        Integer count = userMessageMapper.selectCount(userMessage);
        userMessageRsp.setTotalCount(count);
        userMessageRsp.setCurrentPage(currentPage);
        userMessageRsp.setCurrentCount(limit);
        List<UserMessage> userMessageList = userMessageMapper.selectPageList(userMessage, start, limit);
        userMessageRsp.setUserMessageList(userMessageList);
        return userMessageRsp;
    }


    @Transactional
    public Boolean sendMessage(Integer messageId) {
        UserMessage userMessage = userMessageMapper.selectById(messageId);
        User user = userService.getCurrentUser();
        //记录发送信息
        UserMessageLog userMessageLog = new UserMessageLog();
        userMessageLog.setUserId(user.getId());
        BeanUtils.copyProperties(userMessage, userMessageLog);
        userMessageLog.setStatus(CommonConstant.STATUS_YES);
        userMessageLog.setReadStatus(UserMessageConstant.READ_STATUS_NO);
        userMessageLogMapper.insert(userMessageLog);
        //发送消息
        List<UserPo> userPoList = userManagerMapper.selectAllSecond();
        List<Integer> userList = userPoList.stream().map(e -> e.getId()).collect(Collectors.toList());
        for (Integer userId : userList) {
            userMessageLog.setUserId(userId);
            userMessageLogMapper.insert(userMessageLog);
        }
        return true;
    }

    public UserMessageLogRsp selectMessageLogList(UserMessageLogQueryRequest request) {
        User user = userService.getCurrentUser();
        UserMessageLogRsp UserMessageLogRsp = new UserMessageLogRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;
        UserMessageLog userMessageLog = new UserMessageLog();
        BeanUtils.copyProperties(request, userMessageLog);
        userMessageLog.setUserId(user.getId());
        Integer count = userMessageMapper.selectLogCount(userMessageLog);
        UserMessageLogRsp.setTotalCount(count);
        UserMessageLogRsp.setCurrentPage(currentPage);
        UserMessageLogRsp.setCurrentCount(limit);
        List<UserMessageLog> userMessageLogList = userMessageMapper.selectLogPageList(userMessageLog, start, limit);
        UserMessageLogRsp.setUserMessageLogList(userMessageLogList);
        return UserMessageLogRsp;
    }

    public Boolean readMessage(Integer messageLogId) {
        UserMessageLog userMessageLog = userMessageLogMapper.selectById(messageLogId);
        userMessageLog.setReadStatus(UserMessageConstant.READ_STATUS_YES);
        userMessageLog.setUpdateTime(new Date());
        userMessageLog.setUpdateUser(userService.getCurrentUser().getId());
        return userMessageLogMapper.updateById(userMessageLog) > 0;
    }

    public Boolean delMessageLog(BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = userMessageLogMapper.batchDelete(userId, valueList);
        return result;
    }
}
