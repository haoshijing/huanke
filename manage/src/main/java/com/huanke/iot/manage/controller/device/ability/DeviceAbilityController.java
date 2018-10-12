package com.huanke.iot.manage.controller.device.ability;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.ability.DeviceAbilityService;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilityQueryRequest;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilityVo;
import com.huanke.iot.manage.vo.response.device.ability.DeviceTypeAbilitysVo;
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
public class DeviceAbilityController {

    @Autowired
    private DeviceAbilityService deviceAbilityService;


    /**
     * 添加新能力
     * @param abilityRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新功能")
    @PostMapping(value = "/createDeviceAbility")
    public ApiResponse<Integer> createDeviceAbility(@RequestBody DeviceAbilityCreateOrUpdateRequest abilityRequest) {
        if(StringUtils.isBlank(abilityRequest.getAbilityName()) ||
                StringUtils.isEmpty(abilityRequest.getDirValue())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"功能名称或指令不能为空");
        }
        try{
            ApiResponse<Integer> result =  deviceAbilityService.createOrUpdate(abilityRequest);
            return result;

        }catch (Exception e){
            return new ApiResponse<>(RetCode.ERROR,"保存功能项失败");
        }

    }


    /**
     * 修改新能力
     * @param abilityRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改功能")
    @PutMapping(value = "/updateDeviceAbility")
    public ApiResponse<Integer> updateDeviceAbility(@RequestBody DeviceAbilityCreateOrUpdateRequest abilityRequest) throws Exception{
        try{
            if(StringUtils.isBlank(abilityRequest.getAbilityName()) ||
                    StringUtils.isEmpty(abilityRequest.getDirValue())){
                return new ApiResponse<>(RetCode.PARAM_ERROR,"功能名称或指令不能为空");
            }
            ApiResponse<Integer> result =  deviceAbilityService.createOrUpdate(abilityRequest);
            return result;
        }catch(Exception e){
            return new ApiResponse<>(RetCode.ERROR,"修改功能项失败");

        }

    }



    /**
     * 删除 该能力
     * 删除 能力表中的数据 并删除 选项表中 跟该能力相关的选项
     * @param abilityId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("根据Id删除功能")
    @DeleteMapping(value = "/deleteAbility/{id}")
    public ApiResponse<Boolean> deleteDeviceAbilitySet(@PathVariable("id") Integer abilityId) throws Exception{
        try {
            return  deviceAbilityService.deleteAbility(abilityId);
        } catch (Exception e) {
            return  new ApiResponse<>(RetCode.ERROR,"删除能力项失败");
        }
    }

    /**
     * 查询功能列表
     * @param abilityRequest
     * @return 返回功能项列表
     * @throws Exception
     */
    @ApiOperation("查询功能列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceAbilityVo>> selectList(@RequestBody DeviceAbilityQueryRequest abilityRequest) throws Exception{
        List<DeviceAbilityVo> deviceAbilityVos =  deviceAbilityService.selectList(abilityRequest);
        return new ApiResponse<>(deviceAbilityVos);
    }

    @ApiOperation("查询功能总数")
    @PostMapping(value = "/select/{status}")
    public ApiResponse<Integer> selectCount(@PathVariable("status") Integer status){
        try {
            return this.deviceAbilityService.selectCount(status);
        }catch (Exception e){
            log.error("设备功能总数查询异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"设备功能总数查询失败");
        }
    }
    /**
     * 查询功能列表
     * @param typeId
     * @param abilityType
     * @return 返回功能项列表
     * @throws Exception
     */
    @ApiOperation("根据设备类型和功能项类型 查询功能列表")
    @GetMapping(value = "/selectListByConditions/{typeId}/{abilityType}")
    public ApiResponse<List<DeviceTypeAbilitysVo>> selectListByConditions(@PathVariable("typeId") Integer typeId, @PathVariable("abilityType") Integer abilityType) throws Exception{
        List<DeviceTypeAbilitysVo> deviceTypeAbilitysVos =  deviceAbilityService.selectListByType(typeId,abilityType);
        return new ApiResponse<>(deviceTypeAbilitysVos);
    }

}
