package com.huanke.iot.manage.controller.device.ability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.device.ability.DeviceAbilitySetRelationService;
import com.huanke.iot.manage.service.device.ability.DeviceAbilitySetService;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilitySetQueryRequest;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilitySetVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author caikun
 * @version 2018年08月15日 22:11
 **/
@RestController
@RequestMapping("/api/deviceAbilitySet")
@Slf4j
public class DeviceAbilitySetController {

    @Autowired
    private DeviceAbilitySetService deviceAbilitySetService;

    @Autowired
    private DeviceAbilitySetRelationService deviceAbilitySetRelatoinService;


    /**
     * 添加新能力集
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新功能集")
    @PostMapping(value = "/createDeviceAbilitySet")
    public ApiResponse<Boolean> createDeviceAbilitySet(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAbilitySetService.createOrUpdate(requestParam);
        return new ApiResponse<>(ret);
    }

    /**
     * 修改能力集
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改功能集")
    @RequestMapping(value = "/updateDeviceAbilitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceAbilitySet(@RequestBody String body) throws Exception{
        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAbilitySetService.createOrUpdate(requestParam);
        return new ApiResponse<>(ret);
    }


    /**
     * 删除 能力集
     * 删除能力集表中的数据 并删除 关系表中 与该能力集有关系的数据
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("删除功能集")
    @DeleteMapping(value = "/deleteAbilitySet")
    public ApiResponse<Boolean> deleteDeviceAbilitySet(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAbilitySetService.deleteAbilitySet(requestParam);
        return new ApiResponse<>(ret);
    }

    /**
     * 添加 能力集 中的能力
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加功能集中的功能")
    @RequestMapping(value = "/createDeviceAbilitySetItem",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceAbilitySetRelation(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAbilitySetRelatoinService.createOrUpdate(requestParam);
        return new ApiResponse<>(ret);
    }

    /**
     * 删除 能力集 中的能力
     * 只需要删除关系表中的关系即可
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("根据功能主键，删除功能集中的功能")
    @DeleteMapping(value = "/deleteAbilitySetItemByAbilityId")
    public ApiResponse<Boolean> deleteDeviceAbilitySetItemByAbilityId(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAbilitySetRelatoinService.deleteByAbilityId(requestParam);
        return new ApiResponse<>(ret);
    }


    /**
     * 查询能力集列表
     * @param request
     * @return 返回能力集列表
     * @throws Exception
     */
    @ApiOperation("查询功能集列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceAbilitySetVo>> selectList(@RequestBody DeviceAbilitySetQueryRequest request) throws Exception{
        List<DeviceAbilitySetVo> deviceAbilitySetVos =  deviceAbilitySetService.selectList(request);
        return new ApiResponse<>(deviceAbilitySetVos);
    }

    /**
     * 根据Id 查询能力集
     * @param request
     * @return 返回能力集对象
     * @throws Exception
     */
    @ApiOperation("根据功能集主键 查询功能集")
    @GetMapping(value = "/selectById")
    public ApiResponse<DeviceAbilitySetVo> selectById(@RequestBody DeviceAbilitySetQueryRequest request) throws Exception{
        DeviceAbilitySetVo deviceAbilitySetVo =  deviceAbilitySetService.selectById(request);
        return new ApiResponse<>(deviceAbilitySetVo);
    }

}
