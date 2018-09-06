package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.DeviceFormatRequest;
import com.huanke.iot.api.controller.h5.response.DeviceModelVo;
import com.huanke.iot.api.service.device.format.DeviceFormatService;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
     * 获取H5页面版式及功能项
     * @return
     */
    @RequestMapping("/getModelVo")
    public ApiResponse<DeviceModelVo> getModelVo(@Valid @RequestBody DeviceFormatRequest request) {
        String wxDeviceId = request.getWxDeviceId();
        Integer pageId = request.getPageId();
        log.info("获取h5页面配置项及功能项，wxDeviceId={},pageId={}", wxDeviceId, pageId);
        DeviceModelVo deviceModelVo = deviceFormatService.getModelVo(wxDeviceId, pageId);
        return new ApiResponse<>(deviceModelVo);
    }


}
