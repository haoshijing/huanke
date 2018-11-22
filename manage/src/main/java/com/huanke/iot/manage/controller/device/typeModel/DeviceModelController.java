package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.resp.device.ModelProjectRsp;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.service.device.typeModel.DeviceModelService;
import com.huanke.iot.manage.vo.request.device.operate.DevicePoolRequest;
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

    @Autowired
    private DeviceOperateService deviceOperateService;


    /**
     * 添加新 型号
     *
     * @param modelRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新型号")
    @RequestMapping(value = "/createDeviceModel", method = RequestMethod.POST)
    public ApiResponse<Integer> createDeviceModel(@RequestBody DeviceModelCreateOrUpdateRequest modelRequest) throws Exception {
        ApiResponse<Integer> result = null;
        try {
            if (StringUtils.isBlank(modelRequest.getName())) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "型号名称不能为空");
            }
            if (modelRequest.getCustomerId() == null) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "客户主键不能为空");
            }
            result = deviceModelService.createOrUpdate(modelRequest);
        } catch (Exception e) {
            log.error("添加新型号失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "添加新型号失败");
        }
        return result;
    }


    /**
     * 修改 型号
     *
     * @param modelRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改设备型号")
    @PutMapping(value = "/updateDeviceModel")
    public ApiResponse<Integer> updateDeviceModel(@RequestBody DeviceModelCreateOrUpdateRequest modelRequest) throws Exception {

        ApiResponse<Integer> result = null;
        try {
            if (modelRequest.getId() == null || modelRequest.getId() <= 0) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "型号主键不存在");
            }
            if (StringUtils.isBlank(modelRequest.getName())) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "型号名称不能为空");
            }
            result = deviceModelService.createOrUpdate(modelRequest);
        } catch (Exception e) {
            log.error("修改设备型号失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "修改设备型号失败");
        }

        return result;
    }

    /**
     * 查询型号列表
     *
     * @param modelRequest
     * @return 返回型号项列表
     * @throws Exception
     */
    @ApiOperation("查询设备型号列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceModelVo>> selectList(@RequestBody DeviceModelQueryRequest modelRequest) throws Exception {
        List<DeviceModelVo> deviceModelVos = deviceModelService.selectList(modelRequest);
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

    @ApiOperation("查询型号总数")
    @PostMapping(value = "/selectCount/{status}")
    public ApiResponse<Integer> selcetCount(@PathVariable("status") Integer status) {
        try {
            return this.deviceModelService.selectCount(status);
        } catch (Exception e) {
            log.error("型号总数查询异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "型号总数查询失败");
        }
    }

    /**
     * 根据id查询型号
     *
     * @param id
     * @return 返回型号项列表
     * @throws Exception
     */
    @ApiOperation("根据主键查询设备型号")
    @GetMapping(value = "/selectById/{id}")
    public ApiResponse<DeviceModelVo> selectById(@PathVariable("id") Integer id) throws Exception {
        DeviceModelVo deviceModelVo = deviceModelService.selectById(id);
        return new ApiResponse<>(deviceModelVo);
    }


    /**
     * 根据id 删除 型号
     *
     * @param modelId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("根据id 删除 型号")
    @DeleteMapping(value = "/deleteModelById/{id}")
    public ApiResponse<Boolean> deleteModelById(@PathVariable("id") Integer modelId) throws Exception {
        try {
            return deviceModelService.deleteModelById(modelId);
        } catch (Exception e) {
            log.error("删除设备型号失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "删除设备型号失败");
        }
    }

    /**
     * 根据id 删除 型号（强制删除），若存在被分配，则先进行召回后 再进行删除。
     *
     * @param modelId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("根据id 强制 删除型号")
    @DeleteMapping(value = "/deleteModelByIdForce /{id}")
    public ApiResponse<Boolean> deleteModelByIdForce(@PathVariable("id") Integer modelId) throws Exception {
        Boolean ret = null;
        try {
            return deviceModelService.deleteModelByIdForce(modelId);
        } catch (Exception e) {
            log.error("删除设备型号失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, StringUtils.isNotBlank(e.getMessage())?e.getMessage():"删除设备型号失败");
        }
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

    @ApiOperation("增加设备配额")
    @PostMapping(value = "/createWxDeviceIds")
    public ApiResponse<Integer> createWxDeviceIdPools(DevicePoolRequest devicePoolRequest) {
        try {
            return deviceModelService.createWxDeviceIdPools(devicePoolRequest);
        } catch (Exception e) {
            log.error("增加设备配额失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "增加设备配额失败");
        }
    }

//    @ApiOperation("获取设备的型号功能项")
//    @GetMapping(value = "/getModelVo/{deviceId}")
//    public ApiResponse<List<DeviceModelAbilityVo>> getModelVo(@PathVariable("deviceId") Integer deviceId) {
//
//        List<DeviceModelAbilityVo> deviceModelAbilityVos = deviceModelService.getModelVo(deviceId);
//        return new ApiResponse<>(deviceModelAbilityVos);
//    }
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
//     * @param modelAbilityRequest
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @RequestMapping(value = "/createDeviceModelAbility",method = RequestMethod.POST)
//    public ApiResponse<Boolean> createDeviceModelAbility(@RequestBody DeviceModelCreateOrUpdateRequest.DeviceModelAbilityRequest modelAbilityRequest) throws Exception{
//
//        ApiResponse<Boolean> result =  deviceModelService.createDeviceModelAbility(modelAbilityRequest);
//        return result;
//    }

    @ApiOperation("查客户下模型（工程用）")
    @PostMapping(value = "/selectModelDict")
    public ApiResponse<List<ModelProjectRsp>> selectModelDict() {
        try {
            List<ModelProjectRsp> modelProjectRspList = deviceModelService.selectModelDict();
            return new ApiResponse<>(modelProjectRspList);
        } catch (Exception e) {
            log.error("查询失败", e);
            return new ApiResponse<>(RetCode.ERROR, "查询失败");
        }
    }
}
