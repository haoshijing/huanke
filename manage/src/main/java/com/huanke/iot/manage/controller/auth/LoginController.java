package com.huanke.iot.manage.controller.auth;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
//import com.huanke.iot.manage.controller.AbstractController;
//import com.huanke.iot.manage.controller.request.LoginDataRequest;
//import com.huanke.iot.manage.controller.response.LoginResponse;
//import com.huanke.iot.manage.service.AdminAuthCacheService;
import com.huanke.iot.manage.service.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

//@RestController
//@RequestMapping
//public class LoginController  extends AbstractController{
//    @Autowired
//    private AdminService adminService;
//
//    @Autowired
//    private AdminAuthCacheService adminAuthCacheService;
//
//    @RequestMapping("/login")
//    @ResponseBody
//    public ApiResponse<LoginResponse> login(@RequestBody LoginDataRequest loginDataRequest){
//        LoginResponse loginResponse = new LoginResponse();
//
//        if(StringUtils.isEmpty(loginDataRequest.getName()) ||
//                StringUtils.isEmpty(loginDataRequest.getPassword())){
//            return new ApiResponse<>(RetCode.PARAM_ERROR,"参数错误",loginResponse);
//        }
//        AdminAuthInfo adminAuthInfo = new AdminAuthInfo();
//
//       boolean check =   adminService.checkUser(loginDataRequest.getName(),loginDataRequest.getPassword());
//        if(check){
//            String token = UUID.randomUUID().toString().replace("-","");
//            loginResponse.setToken(token);
//            loginResponse.setSucc(true);
//            adminAuthInfo.setToken(token);
//            adminAuthInfo.setUserName(loginDataRequest.getName());
//            adminAuthCacheService.setTokenCache(token,adminAuthInfo);
//        }
//        return new ApiResponse<>(loginResponse);
//    }
//    @RequestMapping("/logout")
//    @ResponseBody
//    public ApiResponse<Boolean> login(HttpServletRequest request){
//        AdminAuthInfo adminAuthInfo = getToken(request);
//        adminAuthCacheService.deleteToken(adminAuthInfo.getToken());
//        return new ApiResponse<>(true);
//    }
//}
