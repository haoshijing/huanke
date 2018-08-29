package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.controller.h5.response.DeviceListVo;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author onlymark
 * @version 2018年08月28日
 **/
@RequestMapping("/h5/api/format")
@RestController
public class DeviceFormatController {
    @Autowired
    private DeviceDataService deviceDataService;
    /**
     * 设备id查版式
     * @return
     */
    @RequestMapping("/obtainByDeviceId")
    public ApiResponse<DeviceListVo> obtainMyDevice(String deviceId) {
        DeviceDetailVo deviceDetailVo = deviceDataService.queryDetailByDeviceId(deviceId);
        return new ApiResponse<>();
    }
}
