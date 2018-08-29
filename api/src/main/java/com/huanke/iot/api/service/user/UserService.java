package com.huanke.iot.api.service.user;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMacMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceMacPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceMacMapper deviceMacMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private WechartUtil wechartUtil;

    @Async("taskExecutor")
    public void  addOrUpdateUser(String accessToken, String openId){
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(openId);
        CustomerUserPo dbCustomerUserPo = new CustomerUserPo();

        if(customerUserPo != null){
            dbCustomerUserPo.setId(customerUserPo.getId());
        }

        JSONObject userInfo = wechartUtil.obtainUserUserInfo(accessToken,openId);
        if(userInfo != null){
            dbCustomerUserPo.setCity(userInfo.getString("city"));
            dbCustomerUserPo.setHeadimgurl(userInfo.getString("headimgurl"));
            dbCustomerUserPo.setOpenId(userInfo.getString("openid"));
            dbCustomerUserPo.setProvince(userInfo.getString("province"));
            dbCustomerUserPo.setUnionid(userInfo.getString("unionid"));
            dbCustomerUserPo.setSex(userInfo.getInteger("sex"));
            dbCustomerUserPo.setNickname(userInfo.getString("nickname"));
            dbCustomerUserPo.setLastVisitTime(System.currentTimeMillis());
            dbCustomerUserPo.setLastUpdateTime(System.currentTimeMillis());
        }
        if(dbCustomerUserPo.getId() != null){
            customerUserMapper.updateById(dbCustomerUserPo);
        }else{
            dbCustomerUserPo.setCreateTime(System.currentTimeMillis());
            customerUserMapper.insert(dbCustomerUserPo);
        }
    }

    public Integer getUserIdByTicket(String openId) {
        String userIdStr = stringRedisTemplate.opsForValue().get(openId);
        if(StringUtils.isNotEmpty(userIdStr)){
            return Integer.valueOf(userIdStr);
        }
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(openId);
        if(customerUserPo == null){
            log.error(" openId = {} , user is null" ,openId);
            return 0;
        }
        return customerUserPo.getId();
    }

    public Integer getUserIdByIMei(String imei) {


        DeviceMacPo deviceMacPo = deviceMacMapper.selectByMac(imei);
        if(deviceMacPo == null){
            log.error(" imei = {} , data is null" ,imei);
            return 0;
        }
        return deviceMacPo.getAppUserId();
    }
}