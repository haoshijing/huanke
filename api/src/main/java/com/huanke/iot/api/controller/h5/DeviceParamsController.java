package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.DeviceParamConfigRequest;
import com.huanke.iot.api.controller.h5.req.DeviceParamRequest;
import com.huanke.iot.api.controller.h5.response.DeviceParamsVo;
import com.huanke.iot.api.service.device.basic.DeviceParamsService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述:
 * 设备传参controller
 *
 * @author onlymark
 * @create 2018-10-30 上午9:37
 */
@RequestMapping("/h5/params")
@RestController
@Slf4j
public class DeviceParamsController extends BaseController{
    @Autowired
    private DeviceParamsService deviceParamsService;
    /**
     * 判断是否有功能传参设置项
     */
    @RequestMapping("/exist")
    public ApiResponse<Boolean> ifExist(@RequestBody DeviceParamRequest deviceParamRequest){
        Integer deviceId = deviceParamRequest.getDeviceId();
        String typeName = deviceParamRequest.getTypeName();
        if(deviceId == null || typeName == null){
            log.error("参数异常：deviceId={}, typeName={}", deviceId, typeName);
            return new ApiResponse<>(RetCode.ERROR, "参数异常");
        }
        Boolean result = deviceParamsService.ifExist(deviceId, typeName);
        return new ApiResponse<>(result);
    }

    /**
     * 查配置列表
     */
    @RequestMapping("/paramList")
    public ApiResponse<List<DeviceParamsVo>> paramList(@RequestBody DeviceParamRequest deviceParamRequest){
        Integer deviceId = deviceParamRequest.getDeviceId();
        String typeName = deviceParamRequest.getTypeName();
        if(deviceId == null || typeName == null){
            log.error("参数异常：deviceId={}, typeName={}", deviceId, typeName);
            return new ApiResponse<>(RetCode.ERROR, "参数异常");
        }
        List<DeviceParamsVo> deviceParamsVoList = deviceParamsService.paramList(deviceId, typeName);
        return new ApiResponse<>(deviceParamsVoList);
    }

    /**
     * 设置并发送设备传参指令
     */
    @RequestMapping("/sendParamFunc")
    public ApiResponse<String> sendParamFunc(@RequestBody DeviceParamConfigRequest deviceParamConfigRequest){
        Integer currentUserId = getCurrentUserId();
        Integer deviceId = deviceParamConfigRequest.getDeviceId();
        String abilityTypeName = deviceParamConfigRequest.getAbilityTypeName();
        List<DeviceParamConfigRequest.ParamConfig> paramConfigList = deviceParamConfigRequest.getParamConfigList();
        if(deviceId == null || abilityTypeName == null || paramConfigList.size() == 0){
            log.error("参数异常：deviceId={}, abilityTypeName={}", deviceId, abilityTypeName);
            return new ApiResponse<>(RetCode.ERROR, "参数异常");
        }
        String requestId = deviceParamsService.sendParamFunc(currentUserId, deviceId, abilityTypeName, paramConfigList);
        return new ApiResponse<>(requestId);
    }


    /**
     * 轮询查状态接口
     */
    @RequestMapping("/queryDeviceBack")
    public ApiResponse<Boolean> queryDeviceBack(@RequestBody DeviceParamRequest deviceParamRequest){
        Integer deviceId = deviceParamRequest.getDeviceId();
        String typeName = deviceParamRequest.getTypeName();
        if(deviceId == null || typeName == null){
            log.error("参数异常：deviceId={}, typeName={}", deviceId, typeName);
            return new ApiResponse<>(RetCode.ERROR, "参数异常");
        }
        Boolean result = deviceParamsService.queryDeviceBack(deviceId, typeName);
        return new ApiResponse<>(result);
    }
}
