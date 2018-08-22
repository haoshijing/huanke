package com.huanke.iot.manage.controller.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeAblitySetService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeAblitySetCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
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
    private DeviceTypeAblitySetService deviceTypeAblitySetService;

    /**
     * 添加新类型
     * @param typeRrequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新类型")
    @PostMapping(value = "/createDeviceType")
    public ApiResponse<Integer> createDeviceType(@RequestBody DeviceTypeCreateOrUpdateRequest typeRrequest) throws Exception{
        if(StringUtils.isBlank(typeRrequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型名称不能为空");
        }
        return   deviceTypeService.createOrUpdate(typeRrequest);
    }


    /**
     * 修改 类型
     * @param typeRrequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改类型")
    @PutMapping(value = "/updateDeviceType")
    public ApiResponse<Integer> updateDeviceType(@RequestBody DeviceTypeCreateOrUpdateRequest typeRrequest) throws Exception{
        if(typeRrequest.getId()==null||typeRrequest.getId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型主键不存在");
        }
        if(StringUtils.isBlank(typeRrequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"类型名称不能为空");
        }
        return   deviceTypeService.createOrUpdate(typeRrequest);
    }

    /**
     * 删除 类型
     * @param typeId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("删除类型")
    @DeleteMapping(value = "/delteDeviceTypeById/{id}")
    public ApiResponse<Boolean> delteDeviceType(@PathVariable("id") Integer typeId) throws Exception{
        Boolean ret =  deviceTypeService.deleteDeviceType(typeId);
        return new ApiResponse<>(ret);
    }


    /**
     * 添加 类型的功能集
     * @param request
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加类型的功能集")
    @PostMapping(value = "/createDeviceTypeAblitySet")
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
    @ApiOperation("修改类型的功能集")
    @PutMapping(value = "/updateDeviceTypeAblitySet")
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
    @ApiOperation("删除类型的功能集")
    @DeleteMapping(value = "/deleteDeviceTypeAblitySet/{id}")
    public ApiResponse<Boolean> deleteDeviceTypeAblitySet(@RequestBody DeviceTypeAblitySetCreateOrUpdateRequest request) throws Exception{
        Boolean ret = false;
            ret =  deviceTypeAblitySetService.deleteById(request.getId());
        return new ApiResponse<>(ret);
    }

    /**
     * 查询类型列表
     * @param typeRequest
     * @return 返回类型列表
     * @throws Exception
     */
    @ApiOperation("查询类型列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceTypeVo>> selectList(@RequestBody DeviceTypeQueryRequest typeRequest) throws Exception{
        List<DeviceTypeVo> deviceTypeVos =  deviceTypeService.selectList(typeRequest);
        return new ApiResponse<>(deviceTypeVos);
    }

    /**
     * 根据id查询 类型
     * @param typeId
     * @return 返回类型
     * @throws Exception
     */
    @ApiOperation("根据类型主键查询类型")
    @GetMapping(value = "/selectById/{id}")
    public ApiResponse<DeviceTypeVo> selectById(@PathVariable("id")Integer typeId) throws Exception{
        DeviceTypeVo deviceTypeVo =  deviceTypeService.selectById(typeId);
        return new ApiResponse<>(deviceTypeVo);
    }

    /**
     * 根据 类型主键查询 该类型的能力集
     * @param typeId
     * @return
     */
    @ApiOperation("根据类型主键 查询该类型的功能集合")
    @GetMapping(value = "/selectAblitysByTypeId/{typeId}")
    public ApiResponse<List<DeviceAblityVo>>  selectAblitysByTypeId(@PathVariable("typeId")Integer typeId){

        List<DeviceAblityVo> deviceAblityVos = deviceTypeService.selectAblitysByTypeId(typeId);
        return new ApiResponse<>(deviceAblityVos);
    }
}
