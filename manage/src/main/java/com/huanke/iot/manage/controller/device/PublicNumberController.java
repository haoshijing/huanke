package com.huanke.iot.manage.controller.device;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.PublicNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/publicnumber")
public class PublicNumberController {

    @Autowired
    private PublicNumberService publicNumberService;

    @RequestMapping("/obtainList")
    public ApiResponse<List<JSONObject>> obtainList(){
        List<JSONObject> datas = publicNumberService.obtainList();
        return new ApiResponse<>(datas);
    }
}
