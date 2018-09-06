package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.typeModel.DeviceModelService;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation("添加新型号")
    @RequestMapping(value = "/createDeviceModel",method = RequestMethod.POST)
    public ApiResponse<Integer> createDeviceModel(@RequestBody DeviceModelCreateOrUpdateRequest modelRequest) throws Exception{
        if(StringUtils.isBlank(modelRequest.getName()) ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"型号名称不能为空");
        }
        if(modelRequest.getCustomerId()==null ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户主键不能为空");
        }
        ApiResponse<Integer> result =   deviceModelService.createOrUpdate(modelRequest);
        return result;
    }


    /**
     * 修改 型号
     * @param modelRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改设备型号")
    @PutMapping(value = "/updateDeviceModel")
    public ApiResponse<Integer> updateDeviceModel(@RequestBody DeviceModelCreateOrUpdateRequest modelRequest) throws Exception{
        if(modelRequest.getId()==null||modelRequest.getId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"型号主键不存在");
        }
        if(StringUtils.isBlank(modelRequest.getName()) ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"型号名称不能为空");
        }

        ApiResponse<Integer> result =   deviceModelService.createOrUpdate(modelRequest);

        return result;
    }

    /**
     * 查询型号列表
     * @param modelRequest
     * @return 返回型号项列表
     * @throws Exception
     */
    @ApiOperation("查询设备型号列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceModelVo>> selectList(@RequestBody DeviceModelQueryRequest modelRequest) throws Exception{
        List<DeviceModelVo> deviceModelVos =  deviceModelService.selectList(modelRequest);
        return new ApiResponse<>(deviceModelVos);
    }
//
//    /**
//     * 查询类型Id 查询型号列表
//     * @param typeId
//     * @return 返回型号项列表
//     * @throws Exception
//     */
//    @ApiOperation("根据类型Id 查询型号列表")
//    @GetMapping(value = "/selectByTypeId/{typeId}")
//    public ApiResponse<List<DeviceModelVo>> selectByTypeId(@PathVariable("typeId")Integer typeId) throws Exception{
//        List<DeviceModelVo> deviceModelVos =  deviceModelService.selectByTypeId(typeId);
//        return new ApiResponse<>(deviceModelVos);
//    }

    /**
     * 根据id查询型号
     * @param id
     * @return 返回型号项列表
     * @throws Exception
     */
    @ApiOperation("根据主键查询设备型号")
    @GetMapping(value = "/selectById/{id}")
    public ApiResponse<DeviceModelVo> selectById(@PathVariable("id")Integer id) throws Exception{
        DeviceModelVo deviceModelVo =  deviceModelService.selectById(id);
        return new ApiResponse<>(deviceModelVo);
    }


    /**
     * 根据id 删除 型号
     * @param modelId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("根据id 删除 型号")
    @DeleteMapping(value = "/deleteModelById/{id}")
    public ApiResponse<Boolean> deleteModelById(@PathVariable("id") Integer modelId) throws Exception{
        Boolean ret =  deviceModelService.deleteModelById(modelId);
        return new ApiResponse<>(ret);
    }


    @ApiOperation("根据设备类型主键集合，查询所有设备型号")
    @GetMapping(value = "/selectModelsByTypeIds/{typeIds}")
    public ApiResponse<List<DeviceModelVo>> selectModelsByTypeIds(@PathVariable("typeIds") String typeIds) {
        if (typeIds.split(",").length > 0) {
            List<DeviceModelVo> deviceModelVos = deviceModelService.selectModelsByTypeIds(typeIds);
            return new ApiResponse<>(deviceModelVos);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "类型主键格式不正确");
        }
    }

    @ApiOperation("根据设备类型主键集合，查询所有设备型号")
    @PostMapping(value = "/createWxDeviceIds}")
    public ApiResponse<Boolean> createWxDeviceIdPools(@PathVariable("customerId") Integer customerId, @PathVariable("productId")String productId,@PathVariable("addCount") Integer addCount) {
        return  deviceModelService.createWxDeviceIdPools(customerId,productId,addCount);
    }
//    /**
//     * 添加型号的版式配置
//     * @param modelFormatRequests
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @ApiOperation("添加型号的版式配置")
//    @RequestMapping(value = "/createModelFormat",method = RequestMethod.POST)
//    public  ApiResponse<Boolean>  createModelFormat(@RequestBody DeviceModelCreateOrUpdateRequest.DeviceModelFormatRequests modelFormatRequests) throws Exception{
//
//        ApiResponse<Boolean> result =   deviceModelService.createOrUpdateModelFormat(modelFormatRequests);
//        return result;
//    }
//
//    /**
//     * 修改型号的版式配置
//     * @param modelFormatRequests
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @ApiOperation("修改型号的版式配置")
//    @PutMapping(value = "/updateModelFormat")
//    public  ApiResponse<Boolean>  updateModelFormat(@RequestBody DeviceModelCreateOrUpdateRequest.DeviceModelFormatRequests modelFormatRequests) throws Exception{
//
//        ApiResponse<Boolean> result =   deviceModelService.createOrUpdateModelFormat(modelFormatRequests);
//        return result;
//    }
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
