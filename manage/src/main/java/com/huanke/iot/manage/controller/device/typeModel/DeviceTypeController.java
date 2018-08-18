package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeAblitySetService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
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
@RequestMapping("/api/deviceType")
@Slf4j
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    @Autowired
    private DeviceTypeAblitySetService deviceTypeAblitySetService;

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
        if(typeyRequest.getId()==null||typeyRequest.getId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型主键不存在");
        }
        if(StringUtils.isBlank(typeyRequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型名称不能为空");
        }
        Boolean ret =  deviceTypeService.createOrUpdate(typeyRequest);
        return new ApiResponse<>(ret);
    }

    /**
     * 删除 类型
     * @param typeyRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/delteAblityType",method = RequestMethod.POST)
    public ApiResponse<Boolean> delteDeviceType(@RequestBody DeviceTypeCreateOrUpdateRequest typeyRequest) throws Exception{
        if(null==typeyRequest.getId()){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型主键不能为空");        }
        Boolean ret =  deviceTypeService.deleteDeviceType(typeyRequest);
        return new ApiResponse<>(ret);
    }


    /**
     * 添加 类型的功能集
     * @param request
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/createDeviceTypeAblitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceTypeAblitySet(@RequestBody DeviceTypeAblitySetCreateOrUpdateRequest request) throws Exception{
        ApiResponse<Boolean> result =  deviceTypeService.createOrUpdateDeviceTypeAblitySet(request);
        return result;
    }

    /**
     * 修改 类型的功能集
     * @param request
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/updateDeviceTypeAblitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceTypeAblitySet(@RequestBody DeviceTypeAblitySetCreateOrUpdateRequest request) throws Exception{
        ApiResponse<Boolean> result =  deviceTypeService.createOrUpdateDeviceTypeAblitySet(request);
        return result;
    }

    /**
     * 删除 类型的功能集
     * @param request
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/deleteDeviceTypeAblitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> deleteDeviceTypeAblitySet(@RequestBody DeviceTypeAblitySetCreateOrUpdateRequest request) throws Exception{
        Boolean ret = false;
        if(request.getId()!=null&&request.getId()>0){
            ret =  deviceTypeAblitySetService.deleteById(request.getId());
        }else{
            return new ApiResponse<>(RetCode.PARAM_ERROR,"主键不能为空");
        }
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
        List<DeviceTypeVo> deviceTypeVos =  deviceTypeService.selectList(typeRequest);
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
        DeviceTypeVo deviceTypeVo =  deviceTypeService.selectById(modelRequest);
        return new ApiResponse<>(deviceTypeVo);
    }

}
