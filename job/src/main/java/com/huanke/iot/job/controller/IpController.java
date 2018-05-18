package com.huanke.iot.job.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author haoshijing
 * @version 2018年05月14日 10:50
 **/

@RequestMapping("/ip")
@Controller
public class IpController {

    @ResponseBody
    @RequestMapping("/test")
    public String getIp(HttpServletRequest request){
        return request.getRemoteAddr();
    }
}
