package com.huanke.iot.manage.service.info;

import com.huanke.iot.base.dao.customer.UserFeedbackMapper;
import com.huanke.iot.base.po.customer.UserFeedbackPo;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.vo.request.customer.UserFeedbackInfoVoReq;
import com.huanke.iot.manage.vo.response.device.customer.UserFeedbackInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class UserFeedbackInfoService {
    @Autowired
    private UserFeedbackMapper userFeedbackMapper;
    @Autowired
    private CustomerService customerService;
    public List<UserFeedbackInfoVo> selectList(UserFeedbackInfoVoReq userFeedbackInfoVoReq) {
        Integer customerId = 53;//customerService.obtainCustomerId(false);
        userFeedbackInfoVoReq.setCustomerId(customerId);
        List<UserFeedbackPo> userFeedbackPos = userFeedbackMapper.selectListByPara(userFeedbackInfoVoReq);
        List<UserFeedbackInfoVo> userFeedbackInfos = new ArrayList<>();
        userFeedbackPos.stream().forEach(temp->{
            UserFeedbackInfoVo userFeedbackInfo = new UserFeedbackInfoVo();
            userFeedbackInfo.setId(temp.getId());
            userFeedbackInfo.setUserName(temp.getUserName());
            userFeedbackInfo.setLocation(temp.getLocation());
            userFeedbackInfo.setFeedbackInfo(temp.getFeedbackInfo());
            userFeedbackInfo.setCreateTime(temp.getCreateTime());
            userFeedbackInfos.add(userFeedbackInfo);
        });
        return userFeedbackInfos;
    }

}
