package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.response.DeviceModelVo;
import com.huanke.iot.api.service.device.format.DeviceFormatService;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author onlymark
 * @version 2018年08月28日
 **/
@RequestMapping("/h5/api/format")
@RestController
@Slf4j
public class DeviceFormatController {

    @Autowired
    private DeviceFormatService deviceFormatService;

    /**
     * 获取 formatId
     * @return
     */
    @RequestMapping("/getModelVo1")
    public ApiResponse<DeviceModelVo> getModelVo(String deviceId, Integer pageId) {
        DeviceModelVo deviceModelVo = deviceFormatService.getModelVo(deviceId, pageId);
        return new ApiResponse<>(deviceModelVo);
    }


}
