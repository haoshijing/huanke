package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
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
@RequestMapping("/api/deviceType")
@Slf4j
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    @Autowired
    private CustomerService customerService;

    /**
     * 添加新类型
     *
     * @param typeRrequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新类型")
    @PostMapping(value = "/createDeviceType")
    public ApiResponse<Integer> createDeviceType(@RequestBody DeviceTypeCreateOrUpdateRequest typeRrequest) throws Exception {
        if (StringUtils.isBlank(typeRrequest.getName())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "类型名称不能为空");
        }
        try {
            return deviceTypeService.createOrUpdate(typeRrequest);
        } catch (Exception e) {
            log.error("添加新类型失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "添加类型失败");
        }
    }


    /**
     * 修改 类型
     *
     * @param typeRrequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改类型")
    @PutMapping(value = "/updateDeviceType")
    public ApiResponse<Integer> updateDeviceType(@RequestBody DeviceTypeCreateOrUpdateRequest typeRrequest) throws Exception {
        if (typeRrequest.getId() == null || typeRrequest.getId() <= 0) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "类型主键不存在");
        }
        if (StringUtils.isBlank(typeRrequest.getName())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "类型名称不能为空");
        }

        try {
            return deviceTypeService.createOrUpdate(typeRrequest);
        } catch (Exception e) {
            log.error("修改类型失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "修改类型失败");
        }
    }

    /**
     * 删除 类型
     *
     * @param typeId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("删除类型-逻辑删除")
    @DeleteMapping(value = "/deleteDeviceTypeById/{id}")
    public ApiResponse<Boolean> deleteDeviceTypeById(@PathVariable("id") Integer typeId) throws Exception {

        Boolean ret = null;
        try {
            return deviceTypeService.deleteDeviceType(typeId);
        } catch (Exception e) {
            log.error("删除类型失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "删除类型失败");
        }
    }


    /**
     * 删除 类型
     *
     * @param typeId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("删除类型-物理删除")
    @DeleteMapping(value = "/destoryDeviceType/{id}")
    public ApiResponse<Boolean> destoryDeviceType(@PathVariable("id") Integer typeId) throws Exception {
        Boolean ret = null;
        try {
            ret = deviceTypeService.destoryDeviceType(typeId);
        } catch (Exception e) {
            log.error("删除类型失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "删除类型失败");
        }
        return new ApiResponse<>(ret);
    }


//    /**
//     * 添加 类型的功能集
//     * @param request
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @ApiOperation("添加类型的功能集")
//    @PostMapping(value = "/createDeviceTypeAbilitySet")
//    public ApiResponse<Boolean> createDeviceTypeAbilitySet(@RequestBody DeviceTypeAbilitySetCreateOrUpdateRequest request) throws Exception{
//        ApiResponse<Boolean> result =  deviceTypeService.createOrUpdateDeviceTypeAbilitySet(request);
//        return result;
//    }
//
//    /**
//     * 修改 类型的功能集
//     * @param request
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @ApiOperation("修改类型的功能集")
//    @PutMapping(value = "/updateDeviceTypeAbilitySet")
//    public ApiResponse<Boolean> updateDeviceTypeAbilitySet(@RequestBody DeviceTypeAbilitySetCreateOrUpdateRequest request) throws Exception{
//        ApiResponse<Boolean> result =  deviceTypeService.createOrUpdateDeviceTypeAbilitySet(request);
//        return result;
//    }
//
//    /**
//     * 删除 类型的功能集
//     * @param request
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @ApiOperation("删除类型的功能集")
//    @DeleteMapping(value = "/deleteDeviceTypeAbilitySet/{id}")
//    public ApiResponse<Boolean> deleteDeviceTypeAbilitySet(@RequestBody DeviceTypeAbilitySetCreateOrUpdateRequest request) throws Exception{
//        Boolean ret = false;
//            ret =  deviceTypeAbilitySetService.deleteById(request.getId());
//        return new ApiResponse<>(ret);
//    }

    /**
     * 查询类型列表
     *
     * @param typeRequest
     * @return 返回类型列表
     * @throws Exception
     */
    @ApiOperation("查询类型列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceTypeVo>> selectList(@RequestBody DeviceTypeQueryRequest typeRequest) throws Exception {
        List<DeviceTypeVo> deviceTypeVos = deviceTypeService.selectList(typeRequest);
        return new ApiResponse<>(deviceTypeVos);
    }

    @ApiOperation("查询类型总数")
    @PostMapping(value = "/selectCount/{status}")
    public ApiResponse<Integer> selectCount(@PathVariable("status") Integer status) {
        try {
            return this.deviceTypeService.selectCount(status);
        } catch (Exception e) {
            log.error("设备类型总数查询异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备类型总数查询失败");
        }
    }

    /**
     * 根据id查询 类型
     *
     * @param typeId
     * @return 返回类型
     * @throws Exception
     */
    @ApiOperation("根据类型主键查询类型")
    @GetMapping(value = "/selectById/{id}")
    public ApiResponse<DeviceTypeVo> selectById(@PathVariable("id") Integer typeId) throws Exception {
        DeviceTypeVo deviceTypeVo = deviceTypeService.selectById(typeId);
        return new ApiResponse<>(deviceTypeVo);
    }

    /**
     * 查询所有设备类型 包含微信h5端配置信息
     *
     * @param typeRequest
     * @return 返回类型列表
     * @throws Exception
     */
    @ApiOperation("查询所有设备类型 包含微信h5端配置信息")
    @PostMapping(value = "/selectAllTypes")
    public ApiResponse<List<DeviceTypeVo>> selectAllTypes(@RequestBody DeviceTypeQueryRequest typeRequest) throws Exception {
        List<DeviceTypeVo> deviceTypeVos = deviceTypeService.selectList(typeRequest);
        return new ApiResponse<>(deviceTypeVos);
    }

    @ApiOperation("根据设备类型主键集合，查询所有设备类型 ")
    @GetMapping(value = "/selectListByTypeIds/{typeIds}")
    public ApiResponse<List<DeviceTypeVo>> selectListByTypeIds(@PathVariable("typeIds") String typeIds) {
        if (null!=typeIds&&typeIds.split(",").length > 0) {
            List<DeviceTypeVo> deviceTypeVos = deviceTypeService.selectListByTypeIds(typeIds);
            return new ApiResponse<>(deviceTypeVos);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "类型主键格式不正确");
        }
    }


    @ApiOperation("根据当前二级域名，查询可用的设备类型 ")
    @GetMapping(value = "/selectTypesBySLD")
    public ApiResponse<List<DeviceTypeVo>> selectTypesBySLD() {

        Integer customerId = customerService.obtainCustomerId(false);
        String typeIds = null;
        if(customerId!=null){
            CustomerVo curCustomerVo = customerService.selectById(customerId);
            typeIds = curCustomerVo.getTypeIds();
            if (null!=typeIds&&typeIds.split(",").length > 0) {
                List<DeviceTypeVo> deviceTypeVos = deviceTypeService.selectListByTypeIds(typeIds);
                return new ApiResponse<>(deviceTypeVos);
            }else{
                return new ApiResponse<>(RetCode.PARAM_ERROR, "当前客户，配置的类型格式不正确");
            }
        }else{
            List<DeviceTypeVo> deviceTypeVos = deviceTypeService.selectAllTypes();
            return new ApiResponse<>(deviceTypeVos);
        }

    }


//    /**
//     * 根据 类型主键查询 该类型的能力集
//     * @param typeId
//     * @return
//     */
//    @ApiOperation("根据类型主键 查询该类型的功能集合")
//    @GetMapping(value = "/selectAbilitysByTypeId/{typeId}")
//    public ApiResponse<List<DeviceAbilityVo>>  selectAbilitysByTypeId(@PathVariable("typeId")Integer typeId){
//
//        List<DeviceAbilityVo> deviceAbilityVos = deviceTypeService.selectAbilitysByTypeId(typeId);
//        return new ApiResponse<>(deviceAbilityVos);
//    }
}
