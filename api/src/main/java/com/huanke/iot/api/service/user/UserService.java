package com.huanke.iot.api.service.user;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMacMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceMacPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    private CustomerMapper customerMapper;

    @Autowired
    private WechartUtil wechartUtil;

    @Async("taskExecutor")
    public void  addOrUpdateUser(String accessToken, String openId, Integer customerId){
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
            dbCustomerUserPo.setCustomerId(customerId);
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

//    /**
//     * 用户关注公众号，新增用户
//     */
//    public void attentionPublicNumber(HttpServletRequest request, Map<String, String> requestMap, String event){
////                reqMap = {CreateTime=1538207654, EventKey=, Event=subscribe, ToUserName=gh_f5f0fdaff947,
//// FromUserName=oOSkz0XQn4q-6HWEpfeNRaSbkhY4, MsgType=event}
//
//        // reqMap = {DeviceType=gh_7f3ba47c70a3, DeviceID=gh_7f3ba47c70a3_f1c1cd2015ab27b6,
//        // Content=, CreateTime=1523200569, Event=unbind, ToUserName=gh_7f3ba47c70a3,
//        // FromUserName=okOTjwpDwxJR666hVWnj_L_jp87w,
//        // MsgType=device_event, SessionID=0, OpenID=okOTjwpDwxJR666hVWnj_L_jp87w}
//        String openId = requestMap.get("FromUserName");
//        CustomerUserPo queryCustomerUserPo = getUserByTicket(openId);
//        if(null==queryCustomerUserPo){
//
//            CustomerUserPo newCustomerUserPo = new CustomerUserPo();
//            newCustomerUserPo.setCreateTime(System.currentTimeMillis());
//            newCustomerUserPo.setOpenId(openId);
//            newCustomerUserPo.setCustomerId(customerId);
//            customerUserMapper.insert(newCustomerUserPo);
//            userId = newCustomerUserPo.getId();
//        }
//
//    }

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

    public CustomerUserPo getUserByTicket(String openId) {
        String userIdStr = stringRedisTemplate.opsForValue().get(openId);
        if(StringUtils.isNotEmpty(userIdStr)){
            return null;
        }
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(openId);
        if(customerUserPo == null){
            log.error(" openId = {} , user is null" ,openId);
            return null;
        }
        return customerUserPo;
    }

    public CustomerUserPo getUserByIMeiAndAppId(String imei,String appid) {
//        DeviceMacPo deviceMacPo = deviceMacMapper.selectByMac(imei);
//        if(deviceMacPo == null){
//            log.error(" imei = {} , data is null" ,imei);
//            return 0;
//        }
//        return deviceMacPo.getAppUserId();
        CustomerPo customerPo = customerMapper.selectByAppId(appid);
        CustomerUserPo customerUserPo = new CustomerUserPo();
        customerUserPo.setCustomerId(customerPo.getId());
        customerUserPo.setMac(imei);
        List<CustomerUserPo> customerUserPos = customerUserMapper.selectList(customerUserPo, 1000, 0);
        if(customerUserPos == null || customerUserPos.size()<1){
            log.info(" imei = {} , data is null" ,imei);
            return null;
        }
        return customerUserPos.get(0);
    }
    public CustomerPo getCustomerByOpenId(String openId){
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(openId);
        CustomerPo customerPo = customerMapper.selectById(customerUserPo.getCustomerId());
        return customerPo;
    }
}