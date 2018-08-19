package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.typeModel.DeviceModelService;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
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
@RequestMapping("/api/deviceModel")
@Slf4j
public class DeviceModelController {

    @Autowired
    private DeviceModelService deviceModelService;


    /**
     * 添加新 型号
     * @param modelRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/createDeviceModel",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceModel(@RequestBody DeviceModelCreateOrUpdateRequest modelRequest) throws Exception{
        if(StringUtils.isBlank(modelRequest.getName()) ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"型号名称不能为空");
        }
        if(modelRequest.getCustomerId()==null ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户主键不能为空");
        }
        Boolean ret =  deviceModelService.createOrUpdate(modelRequest);
        return new ApiResponse<>(ret);
    }


    /**
     * 修改 型号
     * @param modelRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/updateDeviceModel",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceModel(@RequestBody DeviceModelCreateOrUpdateRequest modelRequest) throws Exception{
        if(modelRequest.getId()==null||modelRequest.getId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"型号主键不存在");
        }
        if(StringUtils.isBlank(modelRequest.getName()) ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"型号名称不能为空");
        }
        Boolean ret =  deviceModelService.createOrUpdate(modelRequest);
        return new ApiResponse<>(ret);
    }

    /**
     * 查询型号列表
     * @param modelRequest
     * @return 返回型号项列表
     * @throws Exception
     */
    @RequestMapping(value = "/select")
    public ApiResponse<List<DeviceModelVo>> selectList(@RequestBody DeviceModelQueryRequest modelRequest) throws Exception{
        List<DeviceModelVo> deviceModelVos =  deviceModelService.selectList(modelRequest);
        return new ApiResponse<>(deviceModelVos);
    }

    /**
     * 查询类型Id 查询型号列表
     * @param modelRequest
     * @return 返回型号项列表
     * @throws Exception
     */
    @RequestMapping(value = "/selectByTypeId")
    public ApiResponse<List<DeviceModelVo>> selectByTypeId(@RequestBody DeviceModelQueryRequest modelRequest) throws Exception{
        List<DeviceModelVo> deviceModelVos =  deviceModelService.selectByTypeId(modelRequest);
        return new ApiResponse<>(deviceModelVos);
    }

    /**
     * 根据id查询型号
     * @param modelRequest
     * @return 返回型号项列表
     * @throws Exception
     */
    @RequestMapping(value = "/selectById")
    public ApiResponse<DeviceModelVo> selectById(@RequestBody DeviceModelQueryRequest modelRequest) throws Exception{
        DeviceModelVo deviceModelVo =  deviceModelService.selectById(modelRequest);
        return new ApiResponse<>(deviceModelVo);
    }


//
//    /**
//     * 添加 型号的能力
//     * @param modelAblityRequest
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @RequestMapping(value = "/createDeviceModelAblity",method = RequestMethod.POST)
//    public ApiResponse<Boolean> createDeviceModelAblity(@RequestBody DeviceModelCreateOrUpdateRequest.DeviceModelAblityRequest modelAblityRequest) throws Exception{
//
//        ApiResponse<Boolean> result =  deviceModelService.createDeviceModelAblity(modelAblityRequest);
//        return result;
//    }
}
