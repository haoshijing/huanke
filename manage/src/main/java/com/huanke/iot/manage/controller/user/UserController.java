package com.huanke.iot.manage.controller.user;

import com.google.common.collect.Lists;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
//import com.huanke.iot.manage.controller.AbstractController;
//import com.huanke.iot.manage.controller.request.UpdatePwdRequest;
//import com.huanke.iot.manage.controller.response.UserDataResponse;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.manage.service.AdminService;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.vo.request.BaseRequest;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("修改用户大数据看板地址信息")
    @PostMapping(value = "updataLocation")
    public ApiResponse<Boolean> updataLocation(@RequestBody BaseRequest<String> request) {
        User currentUser = userService.getCurrentUser();
        userService.updataLocation(currentUser.getId(),request.getValue());
        return null;
    }
}
