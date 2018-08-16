package com.huanke.iot.manage.controller.device.ablity;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.ablity.DeviceAblityService;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月15日 22:11
 **/
@RestController
@RequestMapping("/api/deviceAbility")
@Slf4j
public class DeviceAblityController {

    @Autowired
    private DeviceAblityService deviceAblityService;


    /**
     * 添加新能力
     * @param ablityRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/createDeviceAblity",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceAblity(@RequestBody DeviceAblityCreateOrUpdateRequest ablityRequest) throws Exception{
        if(StringUtils.isBlank(ablityRequest.getAblityName()) ||
                StringUtils.isEmpty(ablityRequest.getDirValue())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"功能名称或指令不能为空");
        }
        Boolean ret =  deviceAblityService.createOrUpdate(ablityRequest);
        return new ApiResponse<>(ret);
    }


    /**
     * 修改新能力
     * @param ablityRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/updateDeviceAblity",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceAblity(@RequestBody DeviceAblityCreateOrUpdateRequest ablityRequest) throws Exception{
        if(StringUtils.isBlank(ablityRequest.getAblityName()) ||
                StringUtils.isEmpty(ablityRequest.getDirValue())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"功能名称或指令不能为空");
        }
        Boolean ret =  deviceAblityService.createOrUpdate(ablityRequest);
        return new ApiResponse<>(ret);
    }


    /**
     * 查询功能列表
     * @param ablityRequest
     * @return 返回功能项列表
     * @throws Exception
     */
    @RequestMapping(value = "/select")
    public ApiResponse<List<DeviceAblityVo>> selectList(@RequestBody DeviceAblityQueryRequest ablityRequest) throws Exception{
        List<DeviceAblityVo> deviceAblityVos =  deviceAblityService.selectList(ablityRequest);
        return new ApiResponse<>(deviceAblityVos);
    }

}
