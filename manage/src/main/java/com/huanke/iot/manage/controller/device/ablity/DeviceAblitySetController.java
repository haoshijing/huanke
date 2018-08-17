package com.huanke.iot.manage.controller.device.ablity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.ablity.DeviceAblitySetService;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblitySetQueryRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeAblitySetQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblitySetVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author caikun
 * @version 2018年08月15日 22:11
 **/
@RestController
@RequestMapping("/api/deviceAbilitySet")
@Slf4j
public class DeviceAblitySetController {

    @Autowired
    private DeviceAblitySetService deviceAblitySetService;


    /**
     * 添加新能力集
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/createDeviceAblitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceAblitySet(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAblitySetService.createOrUpdate(requestParam);
        return new ApiResponse<>(ret);
    }


    /**
     * 修改能力集
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/updateDeviceAblitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceAblitySet(@RequestBody String body) throws Exception{
        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAblitySetService.createOrUpdate(requestParam);
        return new ApiResponse<>(ret);
    }


    /**
     * 查询能力集列表
     * @param request
     * @return 返回能力集列表
     * @throws Exception
     */
    @RequestMapping(value = "/select")
    public ApiResponse<List<DeviceAblitySetVo>> selectList(@RequestBody DeviceAblitySetQueryRequest request) throws Exception{
        List<DeviceAblitySetVo> deviceAblitySetVos =  deviceAblitySetService.selectList(request);
        return new ApiResponse<>(deviceAblitySetVos);
    }

    /**
     * 根据Id 查询能力集
     * @param request
     * @return 返回能力集对象
     * @throws Exception
     */
    @RequestMapping(value = "/selectById")
    public ApiResponse<DeviceAblitySetVo> selectById(@RequestBody DeviceAblitySetQueryRequest request) throws Exception{
        DeviceAblitySetVo deviceAblitySetVo =  deviceAblitySetService.selectById(request);
        return new ApiResponse<>(deviceAblitySetVo);
    }

}
