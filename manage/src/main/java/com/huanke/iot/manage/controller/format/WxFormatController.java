package com.huanke.iot.manage.controller.format;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.typeModel.DeviceModelService;
import com.huanke.iot.manage.service.format.WxFormatService;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import com.huanke.iot.manage.vo.response.format.WxFormatVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月25日 22:11
 **/
@RestController
@RequestMapping("/api/wxFormat")
@Slf4j
public class WxFormatController {

    @Autowired
    private WxFormatService wxFormatService;


    /**
     * 添加新 版式
     * @param wxFormatVo
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新版式")
    @RequestMapping(value = "/createWxFormat",method = RequestMethod.POST)
    public ApiResponse<Integer> createWxFormat(@RequestBody WxFormatVo wxFormatVo) throws Exception{
        if(StringUtils.isBlank(wxFormatVo.getName()) ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"版式名称不能为空");
        }
        ApiResponse<Integer> result =   wxFormatService.createOrUpdate(wxFormatVo);
        return  result;
    }


    /**
     * 修改 版式
     * @param wxFormatVo
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("修改版式")
    @PutMapping(value = "/updateWxFormat")
    public ApiResponse<Integer> updateDeviceModel(@RequestBody WxFormatVo wxFormatVo) throws Exception{
        if(wxFormatVo.getId()==null||wxFormatVo.getId()<=0){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"版式主键不存在");
        }
        if(StringUtils.isBlank(wxFormatVo.getName()) ){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"版式名称不能为空");
        }
        ApiResponse<Integer> result =   wxFormatService.createOrUpdate(wxFormatVo);
        return  result;
    }


}
