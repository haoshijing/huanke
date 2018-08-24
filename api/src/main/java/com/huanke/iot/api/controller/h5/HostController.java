package com.huanke.iot.api.controller.h5;

import com.huanke.iot.base.api.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HostController extends BaseController {

    @RequestMapping("/obtainCurrentAppId")
    public ApiResponse<String> obtainHost(){
        return new ApiResponse<>(getCurrentCustomerAppId());
    }
}
