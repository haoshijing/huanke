package com.huanke.iot.manage.controller.device.ability;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.ability.DeviceParamsService;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsConfigVo;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsConfigVoRequest;
import com.huanke.iot.manage.vo.request.device.ability.DeviceParamsVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuxiaoyu
 * @version 2018年10月29日 14:11
 **/
@RestController
@RequestMapping("/api/deviceParams")
@Slf4j
public class DeviceParamsController {

    @Autowired
    private DeviceParamsService deviceParamsService;


    /**
     * 根据设备查已配置的功能项
     */
    @ApiOperation("设备查功能项")
    @PostMapping(value = "/queryAbilitys/{deviceId}")
    public ApiResponse<List<DeviceParamsVo>> queryAbilitys(@PathVariable("deviceId") Integer deviceId) {
        List<DeviceParamsVo> deviceParamsVoList = deviceParamsService.queryParamsAbility(deviceId);
        return new ApiResponse<>(deviceParamsVoList);
    }

    /**
     * 根据设备查已存在参数配置详情
     */
    @ApiOperation("设备查传参配置详情")
    @PostMapping(value = "/queryParamConfig/{deviceId}")
    public ApiResponse<List<DeviceParamsConfigVo>> queryParamConfig(@PathVariable("deviceId") Integer deviceId) {
        List<DeviceParamsConfigVo> deviceParamsConfigList = deviceParamsService.queryParamConfig(deviceId);
        return new ApiResponse<>(deviceParamsConfigList);
    }

    /**
     * 添加设备传参配置项
     */
    @ApiOperation("添加设备传参配置项")
    @PostMapping(value = "/addParamConfig")
    public ApiResponse<Boolean> addParamConfig(@RequestBody DeviceParamsConfigVoRequest deviceParamsConfigVoRequest) {
        if (deviceParamsConfigVoRequest.getDeviceId() == null || deviceParamsConfigVoRequest.getDeviceParamsConfigVoList().size() == 0) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "添加参数个数不能为空 ");
        }
        Boolean result = deviceParamsService.addParamConfig(deviceParamsConfigVoRequest);
        return new ApiResponse<>(result);
    }
}
