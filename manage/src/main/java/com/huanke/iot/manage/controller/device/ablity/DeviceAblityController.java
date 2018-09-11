package com.huanke.iot.manage.controller.device.ablity;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.ablity.DeviceAblityService;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceTypeAblitysVo;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation("添加新功能")
    @PostMapping(value = "/createDeviceAblity")
    public ApiResponse<Integer> createDeviceAblity(@RequestBody DeviceAblityCreateOrUpdateRequest ablityRequest) throws Exception{
        if(StringUtils.isBlank(ablityRequest.getAblityName()) ||
                StringUtils.isEmpty(ablityRequest.getDirValue())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"功能名称或指令不能为空");
        }
        try{
            ApiResponse<Integer> result =  deviceAblityService.createOrUpdate(ablityRequest);
            return result;

        }catch (Exception e){
            return new ApiResponse<>(RetCode.ERROR,"保存功能项失败");
        }

    }


    /**
     * 修改新能力
     * @param ablityRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改新功能")
    @PutMapping(value = "/updateDeviceAblity")
    public ApiResponse<Integer> updateDeviceAblity(@RequestBody DeviceAblityCreateOrUpdateRequest ablityRequest) throws Exception{
        if(StringUtils.isBlank(ablityRequest.getAblityName()) ||
                StringUtils.isEmpty(ablityRequest.getDirValue())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"功能名称或指令不能为空");
        }
        ApiResponse<Integer> result =  deviceAblityService.createOrUpdate(ablityRequest);
        return result;
    }



    /**
     * 删除 该能力
     * 删除 能力表中的数据 并删除 选项表中 跟该能力相关的选项
     * @param ablityId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("根据Id删除功能")
    @DeleteMapping(value = "/deleteAblity/{id}")
    public ApiResponse<Boolean> deleteDeviceAblitySet(@PathVariable("id") Integer ablityId) throws Exception{
        return  deviceAblityService.deleteAblity(ablityId);
    }

    /**
     * 查询功能列表
     * @param ablityRequest
     * @return 返回功能项列表
     * @throws Exception
     */
    @ApiOperation("查询功能列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceAblityVo>> selectList(@RequestBody DeviceAblityQueryRequest ablityRequest) throws Exception{
        List<DeviceAblityVo> deviceAblityVos =  deviceAblityService.selectList(ablityRequest);
        return new ApiResponse<>(deviceAblityVos);
    }

    /**
     * 查询功能列表
     * @param typeId
     * @param ablityType
     * @return 返回功能项列表
     * @throws Exception
     */
    @ApiOperation("根据设备类型和功能项类型 查询功能列表")
    @GetMapping(value = "/selectListByConditions/{typeId}/{ablityType}")
    public ApiResponse<List<DeviceTypeAblitysVo>> selectListByConditions(@PathVariable("typeId") Integer typeId, @PathVariable("ablityType") Integer ablityType) throws Exception{
        List<DeviceTypeAblitysVo> deviceTypeAblitysVos =  deviceAblityService.selectListByType(typeId,ablityType);
        return new ApiResponse<>(deviceTypeAblitysVos);
    }

}
