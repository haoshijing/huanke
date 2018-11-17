package com.huanke.iot.api.service.user;

import com.huanke.iot.api.controller.h5.req.UserFeedbackRequest;
import com.huanke.iot.base.dao.customer.UserFeedbackMapper;
import com.huanke.iot.base.po.customer.UserFeedbackPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class UserFeedbackService {
    @Autowired
    private UserFeedbackMapper userFeedbackMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Transactional
    public String saveUserFeedback(Integer userId , UserFeedbackRequest userFeedbackRequest){
        if(stringRedisTemplate.opsForValue().get("userFeedback."+userId)==null){
            userFeedbackRequest.setFeedbackInfo(filter(userFeedbackRequest.getFeedbackInfo()));
            UserFeedbackPo userFeedbackPo = new UserFeedbackPo();
            userFeedbackPo.setUserId(userId);
            userFeedbackPo.setDeviceId(userFeedbackRequest.getDeviceId());
            userFeedbackPo.setFeedbackInfo(userFeedbackRequest.getFeedbackInfo());
            userFeedbackPo.setCreateTime(System.currentTimeMillis());
            userFeedbackMapper.insert(userFeedbackPo);
            stringRedisTemplate.opsForValue().set("userFeedback."+userId, "1");
            stringRedisTemplate.expire("userFeedback."+userId, 60, TimeUnit.SECONDS);
            return "感谢您提供的宝贵意见！";
        }else{
            return "您的手机卡了，请稍后重新填写，感谢您的支持！";
        }
    }
    public String filter(String input){
        return input;
    }
}
