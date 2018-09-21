package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.DeviceFormatRequest;
import com.huanke.iot.api.controller.h5.response.DeviceModelVo;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.api.service.device.format.DeviceFormatService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
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
public class DeviceFormatController extends BaseController{

    @Autowired
    private DeviceFormatService deviceFormatService;

    @Autowired
    private DeviceDataService deviceDataService;

    /**
     * 获取H5页面版式及功能项
     * @return
     */
    @RequestMapping("/getModelVo")
    public ApiResponse<DeviceModelVo> getModelVo(@Valid @RequestBody DeviceFormatRequest request) {
        Integer deviceId = request.getDeviceId();
        Integer pageId = request.getPageNo();
        Integer userId = getCurrentUserId();
        if(!deviceDataService.verifyUser(userId, deviceId)){
            return new ApiResponse<>(RetCode.ERROR, "用户设备不匹配，无法操作");
        }
        log.info("获取h5页面配置项及功能项，deviceId={},pageId={}", deviceId, pageId);
        DeviceModelVo deviceModelVo = deviceFormatService.getModelVo(deviceId, pageId);
        return new ApiResponse<>(deviceModelVo);
    }


}
