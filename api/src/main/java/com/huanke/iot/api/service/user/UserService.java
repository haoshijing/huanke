package com.huanke.iot.api.service.user;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.dao.device.DeviceMacMapper;
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
    private AppUserMapper appUserMapper;

    @Autowired
    private DeviceMacMapper deviceMacMapper;

    @Autowired
    private WechartUtil wechartUtil;

    @Async("taskExecutor")
    public void  addOrUpdateUser(String accessToken, String openId){
        AppUserPo appUserPo = appUserMapper.selectByOpenId(openId);
        AppUserPo dbAppUserPo = new AppUserPo();
        if(appUserPo != null){
            dbAppUserPo.setId(appUserPo.getId());
        }

        JSONObject userInfo = wechartUtil.obtainUserUserInfo(accessToken,openId);
        if(userInfo != null){
            dbAppUserPo.setCity(userInfo.getString("city"));
            dbAppUserPo.setHeadimgurl(userInfo.getString("headimgurl"));
            dbAppUserPo.setOpenId(userInfo.getString("openid"));
            dbAppUserPo.setProvince(userInfo.getString("province"));
            dbAppUserPo.setUnionid(userInfo.getString("unionid"));
            dbAppUserPo.setSex(userInfo.getInteger("sex"));
            dbAppUserPo.setNickname(userInfo.getString("nickname"));
            dbAppUserPo.setLastVisitTime(System.currentTimeMillis());
            dbAppUserPo.setLastUpdateTime(System.currentTimeMillis());
        }
        if(dbAppUserPo.getId() != null){
            appUserMapper.updateById(dbAppUserPo);
        }else{
            dbAppUserPo.setCreateTime(System.currentTimeMillis());
            appUserMapper.insert(dbAppUserPo);
        }
    }

    public Integer getUserIdByTicket(String openId) {
        String userIdStr = stringRedisTemplate.opsForValue().get(openId);
        if(StringUtils.isNotEmpty(userIdStr)){
            return Integer.valueOf(userIdStr);
        }
        AppUserPo appUserPo = appUserMapper.selectByOpenId(openId);
        if(appUserPo == null){
            log.error(" openId = {} , user is null" ,openId);
            return 0;
        }
        return appUserPo.getId();
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