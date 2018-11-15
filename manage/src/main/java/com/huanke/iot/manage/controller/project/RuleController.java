package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.config.DictQueryRequest;
import com.huanke.iot.base.request.project.RuleRequest;
import com.huanke.iot.base.resp.DictRsp;
import com.huanke.iot.manage.service.project.RuleService;
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
 * 规则管理
 *
 * @author onlymark
 * @create 2018-11-14 下午4:37
 */
@RestController
@RequestMapping("/api/rule")
@Slf4j
public class RuleController {
    @Autowired
    private RuleService ruleService;

    @ApiOperation("查询客户列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<DictRsp> selectList(@RequestBody DictQueryRequest request) throws Exception {
        DictRsp dictRsp = ruleService.selectList(request);
        return new ApiResponse<>(dictRsp);
    }

    @ApiOperation("添加字典信息")
    @PostMapping(value = "/addRule")
    public ApiResponse<Boolean> addRule(@RequestBody RuleRequest request) {
        Boolean result = ruleService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改字典信息")
    @PostMapping(value = "/editDict")
    public ApiResponse<Boolean> editRule(@RequestBody RuleRequest request) {
        Boolean result = ruleService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("删除字典信息")
    @PostMapping(value = "/deleteRule")
    public ApiResponse<Boolean> deleteRule(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要删除设备信息");
        }
        Boolean result = ruleService.deleteDict(valueList);
        return new ApiResponse<>(result);
    }
}
