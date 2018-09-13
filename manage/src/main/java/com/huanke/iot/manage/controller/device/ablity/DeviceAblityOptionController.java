package com.huanke.iot.manage.controller.device.ablity;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.ablity.DeviceAblityOptionService;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityOptionVo;
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
@RequestMapping("/api/deviceAbilityOption")
@Slf4j
public class DeviceAblityOptionController {

    @Autowired
    private DeviceAblityOptionService deviceAblityOptionService;


//    /**
//     * 添加新选项
//     * @param optionRequest
//     * @return 成功返回true，失败返回false
//     * @throws Exception
//     */
//    @RequestMapping(value = "/createOption",method = RequestMethod.POST)
//    public ApiResponse<Boolean> createDeviceAblityOption(@RequestBody DeviceAblityOptionCreateOrUpdateRequest optionRequest) throws Exception{
//        if(StringUtils.isBlank(optionRequest.getOptionName()) ){
//            return new ApiResponse<>(RetCode.PARAM_ERROR,"选项名称不能为空");
//        }
//        if(StringUtils.isBlank(optionRequest.getAblityId()) ){
//            return new ApiResponse<>(RetCode.PARAM_ERROR,"没有关联的能力主键");
//        }
//        Boolean ret =  deviceAblityOptionService.createOrUpdate(optionRequest);
//        return new ApiResponse<>(ret);
//    }
//

    /**
     * 修改能力选项
     *
     * @param optionRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改选项")
    @PutMapping(value = "/updateOption")
    public ApiResponse<Boolean> updateDeviceAblityOption(@RequestBody DeviceAblityOptionCreateOrUpdateRequest optionRequest) throws Exception {
        if (StringUtils.isBlank(optionRequest.getOptionName())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "选项名称不能为空");
        }
//        if(StringUtils.isBlank(optionRequest.getAblityId()) ){
//            return new ApiResponse<>(RetCode.PARAM_ERROR,"没有关联的能力主键");
//        }
        Boolean ret = null;
        try {
            ret = deviceAblityOptionService.createOrUpdate(optionRequest);
        } catch (Exception e) {
            log.error("修改功能选项失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "修改功能选项失败");
        }
        return new ApiResponse<>(ret);
    }


    /**
     * 查询选项列表
     *
     * @param optionRequest
     * @return 返回功能项列表
     * @throws Exception
     */
    @ApiOperation("查询选项列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<DeviceAblityOptionVo>> selectList(@RequestBody DeviceAblityOptionQueryRequest optionRequest) throws Exception {
        List<DeviceAblityOptionVo> deviceAblityOptionVos = deviceAblityOptionService.selectList(optionRequest);
        return new ApiResponse<>(deviceAblityOptionVos);
    }


    /**
     * 删除 该选项
     *
     * @param optionRequest
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("查询选项列表")
    @DeleteMapping(value = "/deleteAblityOption")
    public ApiResponse<Boolean> deleteDeviceAblitySet(@RequestBody DeviceAblityOptionQueryRequest optionRequest) throws Exception {
        Boolean ret = null;
        try {
            if (null == optionRequest.getId()) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "选项主键不能为空");
            }
            ret = deviceAblityOptionService.deleteOptionById(optionRequest);
        } catch (Exception e) {
            log.error("删除功能选项失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "删除功能选项失败");
        }
        return new ApiResponse<>(ret);
    }
}
