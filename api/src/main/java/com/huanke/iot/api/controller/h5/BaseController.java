package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.api.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    UserService userService;
    protected Integer getCurrentUserId(){
        UserRequestContext requestContext = UserRequestContextHolder.get();
        return  requestContext.getCurrentId();
    }

    protected  Integer getCurrentUserIdForApp(){
        UserRequestContext requestContext = UserRequestContextHolder.get();
        return  requestContext.getCurrentId();
    }

    protected  String getCurrentUserOpenId(){
        UserRequestContext requestContext = UserRequestContextHolder.get();
        return  requestContext.getOpenId();
    }

    protected String getCurrentCustomerAppId(){
        UserRequestContext requestContext = UserRequestContextHolder.get();
        return  requestContext.getCustomerVo().getAppId();
    }

    protected Integer getCurrentCustomerId(){
        UserRequestContext requestContext = UserRequestContextHolder.get();
        return  requestContext.getCustomerVo().getCustomerId();
    }
}
