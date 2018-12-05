package com.huanke.iot.manage.controller.config;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.config.DictQueryRequest;
import com.huanke.iot.base.request.config.DictRequest;
import com.huanke.iot.base.resp.DictRsp;
import com.huanke.iot.base.resp.QueryDictRsp;
import com.huanke.iot.manage.service.config.DictService;
import com.huanke.iot.manage.vo.request.BaseListRequest;
import com.huanke.iot.manage.vo.request.BaseRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述:
 * 字典controller
 *
 * @author onlymark
 * @create 2018-11-14 上午9:58
 */
@RestController
@RequestMapping("/api/dict")
@Slf4j
public class DictController {
    @Autowired
    private DictService dictService;

    /**
     * 查询客户列表
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation("查询字典列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<DictRsp> selectList(@RequestBody DictQueryRequest request) throws Exception {
        DictRsp dictRsp = dictService.selectList(request);
        return new ApiResponse<>(dictRsp);
    }

    @ApiOperation("添加字典信息")
    @PostMapping(value = "/addDict")
    public ApiResponse<Boolean> addDict(@RequestBody DictRequest request) {
        Boolean result = dictService.addOrUpdateDict(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改字典信息")
    @PostMapping(value = "/editDict")
    public ApiResponse<Boolean> editDict(@RequestBody DictRequest request) {
        Boolean result = dictService.addOrUpdateDict(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("禁用字典信息")
    @PostMapping(value = "/disableDict")
    public ApiResponse<Boolean> disableDict(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要禁用字典信息");
        }
        Boolean result = dictService.disableDict(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("启用字典信息")
    @PostMapping(value = "/enableDict")
    public ApiResponse<Boolean> enableDict(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要启用字典信息");
        }
        Boolean result = dictService.enableDict(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("删除字典信息")
    @PostMapping(value = "/deleteDict")
    public ApiResponse<Boolean> deleteDict(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要删除字典信息");
        }
        Boolean result = dictService.deleteDict(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查字典")
    @PostMapping(value = "/queryDict")
    public ApiResponse<List<QueryDictRsp>> queryDict(@RequestBody BaseRequest<String> request) {
        List<QueryDictRsp> queryDictRspList = dictService.queryDict(request);
        return new ApiResponse<>(queryDictRspList);
    }
}
