package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
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
@RequestMapping("/api/deviceType")
@Slf4j
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;


    /**
     * 添加新类型
     * @param typeRrequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/createDeviceType",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceType(@RequestBody DeviceTypeCreateOrUpdateRequest typeRrequest) throws Exception{
        if(StringUtils.isBlank(typeRrequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型名称不能为空");
        }
        Boolean ret =  deviceTypeService.createOrUpdate(typeRrequest);
        return new ApiResponse<>(ret);
    }


    /**
     * 修改 类型
     * @param typeyRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/updateDeviceType",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceType(@RequestBody DeviceTypeCreateOrUpdateRequest typeyRequest) throws Exception{
        if(StringUtils.isBlank(typeyRequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型名称不能为空");
        }
        Boolean ret =  deviceTypeService.createOrUpdate(typeyRequest);
        return new ApiResponse<>(ret);
    }


    /**
     * 查询功能列表
     * @param ablityRequest
     * @return 返回功能项列表
     * @throws Exception
     */
    @RequestMapping(value = "/select")
    public ApiResponse<List<DeviceTypeVo>> selectList(@RequestBody DeviceTypeQueryRequest ablityRequest) throws Exception{
        List<DeviceTypeVo> deviceTypeVos =  deviceTypeService.selectList(ablityRequest);
        return new ApiResponse<>(deviceTypeVos);
    }

}
