package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeAblitySetService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeAblitySetCreateOrUpdateRequest;
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
@RequestMapping("/api/deviceTypeAblitySet")
@Slf4j
public class DeviceTypeAblitySetController {

    @Autowired
    private DeviceTypeAblitySetService deviceTypeAblitySetService;


    /**
     * 添加新类型 功能集
     * @param request
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/createTypeAblitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceType(@RequestBody DeviceTypeAblitySetCreateOrUpdateRequest request) throws Exception{
        if(request.getTypeId()==null||request.getTypeId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型主键不存在");
        }
        if(request.getAblitySetId()==null||request.getAblitySetId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"能力集主键不存在");
        }
        Boolean ret =  deviceTypeAblitySetService.createOrUpdate(request);
        return new ApiResponse<>(ret);
    }


    /**
     * 修改 类型
     * @param request
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/updateDeviceType",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceType(@RequestBody DeviceTypeAblitySetCreateOrUpdateRequest request) throws Exception{
        if(request.getTypeId()==null||request.getTypeId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型主键不存在");
        }
        if(request.getAblitySetId()==null||request.getAblitySetId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"能力集主键不存在");
        }
        if(request.getId()==null||request.getId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"主键不存在");
        }
        Boolean ret =  deviceTypeAblitySetService.createOrUpdate(request);
        return new ApiResponse<>(ret);
    }


    /**
     * 查询类型列表
     * @param typeRequest
     * @return 返回功能项列表
     * @throws Exception
     */
    @RequestMapping(value = "/select")
    public ApiResponse<List<DeviceTypeVo>> selectList(@RequestBody DeviceTypeQueryRequest typeRequest) throws Exception{
        List<DeviceTypeVo> deviceTypeVos =  deviceTypeAblitySetService.selectList(typeRequest);
        return new ApiResponse<>(deviceTypeVos);
    }

    /**
     * 根据id查询型号
     * @param modelRequest
     * @return 返回型号项列表
     * @throws Exception
     */
    @RequestMapping(value = "/selectById")
    public ApiResponse<DeviceTypeVo> selectById(@RequestBody DeviceTypeQueryRequest modelRequest) throws Exception{
        DeviceTypeVo deviceTypeVo =  deviceTypeAblitySetService.selectById(modelRequest);
        return new ApiResponse<>(deviceTypeVo);
    }

}
