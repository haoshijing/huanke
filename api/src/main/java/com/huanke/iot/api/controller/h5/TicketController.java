package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.util.SignUtils2;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/h5/api")
public class TicketController {
    @Autowired
    private  SignUtils2 signUtils2;
    @RequestMapping("/getSign")
    public ApiResponse<Map<String,String>> getSign(String url){
        return new ApiResponse<>(signUtils2.sign(url));
    }
}
