package com.huanke.iot.api.web;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.api.service.user.UserService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Repository
public class UserInterceptor extends HandlerInterceptorAdapter {

    private static final String TICKET = "Ticket";
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerUserMapper customerUserMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String openId = request.getHeader(TICKET);
        if(StringUtils.isNotEmpty(openId)){
            CustomerUserPo userByTicket = userService.getUserByTicket(openId);
            if(userByTicket != null){
                UserRequestContext requestContext = UserRequestContextHolder.get();
                requestContext.setCurrentId(userByTicket.getId());
                requestContext.setOpenId(openId);
                CustomerPo customerPo = customerMapper.selectById(userByTicket.getCustomerId());
                UserRequestContext.CustomerVo customerVo = new UserRequestContext.CustomerVo();
                customerVo.setAppId(customerPo.getAppid());
                customerVo.setCustomerId(customerPo.getId());
                requestContext.setCustomerVo(customerVo);
                //更新用户的最新访问时间
                userByTicket.setLastVisitTime(System.currentTimeMillis());
                this.customerUserMapper.updateLastVisitById(userByTicket);
                return  true;
            }

        }

        ApiResponse apiResponse = new ApiResponse(RetCode.TICKET_ERROR,"没有获取到openId");
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print(JSON.toJSONString(apiResponse));
        return false;
    }
}
