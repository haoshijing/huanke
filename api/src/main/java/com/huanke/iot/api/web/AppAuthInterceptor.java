package com.huanke.iot.api.web;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.api.service.user.UserService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
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
public class AppAuthInterceptor  extends HandlerInterceptorAdapter {
    private static final String TICKET = "Ticket";
    private static final String APPID = "AppId";
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String imei = request.getHeader(TICKET);
        String appId = request.getHeader(APPID);
        if(StringUtils.isNotEmpty(imei)){

            CustomerUserPo customerUserPo = userService.getUserByIMeiAndAppId(imei,appId);
            //判断是否注册
            if(customerUserPo != null){
                CustomerPo customerByOpenId = userService.getCustomerById(customerUserPo.getCustomerId());
                UserRequestContext requestContext = UserRequestContextHolder.get();
                requestContext.setCurrentId(customerUserPo.getId());
                if(customerByOpenId != null){
                    //此处必进入
                    requestContext.setCustomerVo(new UserRequestContext.CustomerVo());
                    requestContext.getCustomerVo().setCustomerId(customerUserPo.getCustomerId());
                    requestContext.getCustomerVo().setAppId(customerByOpenId.getAppid());
                    return  true;
                }
            }
        }

        ApiResponse apiResponse = new ApiResponse(RetCode.TICKET_ERROR,"没有获取到openId");
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.getWriter().print(JSON.toJSONString(apiResponse));
        return false;
    }
}
