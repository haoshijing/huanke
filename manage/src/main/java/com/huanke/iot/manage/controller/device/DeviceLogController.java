package com.huanke.iot.manage.controller.device;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoshijing
 * @version 2018年05月19日 14:49
 **/

@RestController
@RequestMapping("/device/log")
public class DeviceLogController {

    @RequestMapping("/queryList")
    public ApiResponse<DeviceOperLogPo> queryList(){
        return null;
    }

}
