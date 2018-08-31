package com.huanke.iot.manage.controller.format;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.format.WxFormatService;
import com.huanke.iot.manage.vo.request.format.WxFormatQueryRequest;
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



    /**
     * 查询版式列表
     * @param wxFormatQueryRequest
     * @return 返回版式列表
     * @throws Exception
     */
    @ApiOperation("查询版式列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<WxFormatVo>> selectList(@RequestBody WxFormatQueryRequest wxFormatQueryRequest) throws Exception{
        List<WxFormatVo> wxFormatVos =  wxFormatService.selectList(wxFormatQueryRequest);
        return new ApiResponse<>(wxFormatVos);
    }

    /**
     * 根据版式主键查询版式
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation("根据版式主键查询版式")
    @GetMapping(value = "/selectById/{id}")
    public ApiResponse<WxFormatVo> selectById(@PathVariable("id")Integer id) throws Exception{
        WxFormatVo wxFormatVo =  wxFormatService.selectById(id);
        return new ApiResponse<>(wxFormatVo);
    }

    /**
     * 查询客户 可使用的版式列表
     * @param customerId
     * @return 返回版式列表
     * @throws Exception
     */
    @ApiOperation("查询客户可使用的版式列表")
    @GetMapping(value = "/selectFormatsByCustomerId/{customerId}/{typeId}")
    public ApiResponse<List<WxFormatVo>> selectFormatsByCustomerId(@PathVariable("customerId")Integer customerId,@PathVariable("typeId")Integer typeId) throws Exception{
        ApiResponse<List<WxFormatVo>> result  =  wxFormatService.selectFormatsByCustomerId(customerId,typeId);
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
    @DeleteMapping(value = "/deleteById/{id}")
    public ApiResponse<Boolean> deleteDeviceAblitySet(@PathVariable("id") Integer ablityId) throws Exception{
        ApiResponse<Boolean> result =  wxFormatService.deleteById(ablityId);
        return result;
    }
}
