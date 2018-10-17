package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Repository
@Slf4j
public class AppBasicService {
    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private CustomerUserMapper customerUserMapper;
    @Autowired
    private CustomerMapper customerMapper;

    @Transactional
    public ApiResponse<Object> removeIMeiInfo(HttpServletRequest request){
        String appId = request.getParameter("appId");
        String iMei = request.getParameter("iMei");
        log.info("重置iMei，appId={}，iMei={}",appId,iMei);
        boolean respFlag = false;
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo == null){
            log.error("重置iMei，没有该公众号appId={}",appId);
            return new ApiResponse<>(respFlag);
        }
        CustomerUserPo customerUserPo = new CustomerUserPo();
        customerUserPo.setMac(iMei);
        customerUserPo.setCustomerId(customerPo.getId());
        List<CustomerUserPo> customerUserPos = customerUserMapper.selectList(customerUserPo, 1000, 0);
        if(customerUserPos != null && customerUserPos.size()>0){
            //清空对应公众号下绑定的用户关系
            customerUserPos.forEach(customerUserPoTemp ->{
                customerUserPoTemp.setMac("");
                customerUserMapper.updateById(customerUserPoTemp);
            });
        }
        log.info("重置iMei，成功，appId={}，iMei={}",appId,iMei);
        respFlag=true;
        return new ApiResponse<>(respFlag);
    }



    @Transactional
    public ApiResponse<Object> addUserAppInfo(HttpServletRequest request){
        log.info("appAddUser,appId={},iMei={}",request.getParameter("appId"),request.getParameter("iMei"));
        String appId = request.getParameter("appId");
        CustomerPo customerPo = customerMapper.selectByAppId(appId);
        if(customerPo == null){
            log.error("appAddUser,不存在的appId={}",appId);
            return  new ApiResponse<>(false);
        }
        UserRequestContext context = UserRequestContextHolder.get();
        if(context.getCustomerVo() == null){
            context.setCustomerVo(new UserRequestContext.CustomerVo());
        }
        context.getCustomerVo().setAppId(appId);
        context.getCustomerVo().setCustomerId(customerPo.getId());
        JSONObject resp = wechartUtil.obtainAuthAccessToken(request.getParameter("code"));
        if(resp == null || StringUtils.isEmpty(resp.get("openId"))){
            log.error("appAddUser,获取openId异常，code={}，resp={}",request.getParameter("code"),resp);
            return  new ApiResponse<>(false);
        }
        String iMei = request.getParameter("iMei");
        String openId = resp.getString("openId");
        log.info("appAddUser,openId={}",openId);
        CustomerUserPo customerUserPo = new CustomerUserPo();
        customerUserPo.setMac(iMei);
        customerUserPo.setCustomerId(customerPo.getId());
        List<CustomerUserPo> customerUserPos = customerUserMapper.selectList(customerUserPo, 1000, 0);
        while(customerUserPos != null && customerUserPos.size()>0){
            //清空公众号下现有的imei关联，以及脏数据，理论上不会进来
            customerUserPos.forEach(customerUserPoTemp ->{
                customerUserPoTemp.setMac("");
                customerUserMapper.updateById(customerUserPoTemp);
            });
            customerUserPos = customerUserMapper.selectList(customerUserPo, 1000, 0);
        }
        customerUserPo = customerUserMapper.selectByOpenId(openId);
        if(customerUserPo == null){
            log.info("appAddUser,未注册的openId={}",openId);
            return  new ApiResponse<>(false);
        }
        customerUserPo.setMac(iMei);
        customerUserMapper.updateById(customerUserPo);
        log.info("appAddUser,绑定成功，openId={}，iMei={}",openId,iMei);
        return new ApiResponse<>(true);
    }
}
