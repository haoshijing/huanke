package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@Repository
@Slf4j
public class AppBasicService {
    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Transactional
    public boolean addUserAppInfo(HttpServletRequest request){
        JSONObject resp = wechartUtil.obtainAuthAccessToken(request.getParameter("code"));
        String iMei = request.getParameter("iMei");
        String openId = resp.getString("openId");
        CustomerUserPo customerUserPo = customerUserMapper.selectByMac(iMei);
        while(customerUserPo != null){
            customerUserPo.setMac(iMei);
            customerUserMapper.updateById(customerUserPo);
            customerUserPo = customerUserMapper.selectByMac(iMei);
        }
        customerUserPo = customerUserMapper.selectByOpenId(openId);
        customerUserPo.setMac(iMei);
        customerUserMapper.updateById(customerUserPo);
        return true;
    }
}
