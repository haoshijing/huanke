package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    UserService userService;
    Integer getCurrentUserId(HttpServletRequest request){
        return  userService.getUserIdByTicket(request.getHeader("Ticket"));
    }

    protected  Integer getCurrentUserIdForApp(HttpServletRequest request){
        return  userService.getUserIdByIMei(request.getHeader("Ticket"));
    }
}
