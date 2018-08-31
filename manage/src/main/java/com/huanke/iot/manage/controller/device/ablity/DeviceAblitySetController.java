package com.huanke.iot.manage.controller.device.ablity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.ablity.DeviceAblitySetRelationService;
import com.huanke.iot.manage.service.device.ablity.DeviceAblitySetService;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblitySetQueryRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeAblitySetQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblitySetVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class DeviceAblitySetController {

    @Autowired
    private DeviceAblitySetService deviceAblitySetService;

    @Autowired
    private DeviceAblitySetRelationService deviceAblitySetRelatoinService;


    /**
     * 添加新能力集
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新功能集")
    @PostMapping(value = "/createDeviceAblitySet")
    public ApiResponse<Boolean> createDeviceAblitySet(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAblitySetService.createOrUpdate(requestParam);
        return new ApiResponse<>(ret);
    }

    /**
     * 修改能力集
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改功能集")
    @RequestMapping(value = "/updateDeviceAblitySet",method = RequestMethod.POST)
    public ApiResponse<Boolean> updateDeviceAblitySet(@RequestBody String body) throws Exception{
        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAblitySetService.createOrUpdate(requestParam);
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
    @DeleteMapping(value = "/deleteAblitySet")
    public ApiResponse<Boolean> deleteDeviceAblitySet(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAblitySetService.deleteAblitySet(requestParam);
        return new ApiResponse<>(ret);
    }

    /**
     * 添加 能力集 中的能力
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加功能集中的功能")
    @RequestMapping(value = "/createDeviceAblitySetItem",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDeviceAblitySetRelation(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAblitySetRelatoinService.createOrUpdate(requestParam);
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
    @DeleteMapping(value = "/deleteAblitySetItemByAblityId")
    public ApiResponse<Boolean> deleteDeviceAblitySetItemByAblityId(@RequestBody String body) throws Exception{

        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);

        Boolean ret =  deviceAblitySetRelatoinService.deleteByAblityId(requestParam);
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
    public ApiResponse<List<DeviceAblitySetVo>> selectList(@RequestBody DeviceAblitySetQueryRequest request) throws Exception{
        List<DeviceAblitySetVo> deviceAblitySetVos =  deviceAblitySetService.selectList(request);
        return new ApiResponse<>(deviceAblitySetVos);
    }

    /**
     * 根据Id 查询能力集
     * @param request
     * @return 返回能力集对象
     * @throws Exception
     */
    @ApiOperation("根据功能集主键 查询功能集")
    @GetMapping(value = "/selectById")
    public ApiResponse<DeviceAblitySetVo> selectById(@RequestBody DeviceAblitySetQueryRequest request) throws Exception{
        DeviceAblitySetVo deviceAblitySetVo =  deviceAblitySetService.selectById(request);
        return new ApiResponse<>(deviceAblitySetVo);
    }

}
