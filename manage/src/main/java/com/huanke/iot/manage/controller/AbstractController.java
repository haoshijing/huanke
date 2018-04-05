package com.huanke.iot.manage.controller;

import com.huanke.iot.manage.service.AdminAuthCacheService;
import com.huanke.iot.manage.vo.AdminAuthInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class AbstractController {
    @Autowired
    protected AdminAuthCacheService adminAuthCacheService;
    private static  final String TOKEN = "X-TOKEN";
    public AdminAuthInfo getToken(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader(TOKEN);
        return adminAuthCacheService.getByToken(token);
    }
}
