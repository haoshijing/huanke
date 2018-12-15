package com.huanke.iot.manage.controller.project;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.project.RuleQueryRequest;
import com.huanke.iot.base.request.project.RuleRequest;
import com.huanke.iot.base.resp.project.RuleDictRsp;
import com.huanke.iot.base.resp.project.RuleRsp;
import com.huanke.iot.manage.service.project.RuleService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @ApiOperation("查询规则列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<RuleRsp> selectList(@RequestBody RuleQueryRequest request) throws Exception {
        RuleRsp ruleRsp = ruleService.selectList(request);
        return new ApiResponse<>(ruleRsp);
    }

    @ApiOperation("添加规则信息")
    @PostMapping(value = "/addRule")
    public ApiResponse<Boolean> addRule(@RequestBody RuleRequest request) {
        Boolean result = ruleService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("修改规则信息")
    @PostMapping(value = "/editRule")
    public ApiResponse<Boolean> editRule(@RequestBody RuleRequest request) {
        Boolean result = ruleService.addOrUpdate(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查询可关联功能项")
    @GetMapping(value = "/getEnableAbility")
    public ApiResponse<Map> getEnableAbility() {
        Map<Integer, String> result = ruleService.getEnableAbility();
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量启用规则")
    @PostMapping(value = "/reverseRule")
    public ApiResponse<Boolean> reverseRule(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要恢复规则信息");
        }
        Boolean result = ruleService.reverseRule(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量删除规则")
    @PostMapping(value = "/deleteRule")
    public ApiResponse<Boolean> deleteRule(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要删除规则信息");
        }
        Boolean result = ruleService.deleteRule(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("批量禁用规则")
    @PostMapping(value = "/forbitRule")
    public ApiResponse<Boolean> forbitRule(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要禁用规则信息");
        }
        Boolean result = ruleService.forbitRule(valueList);
        return new ApiResponse<>(result);
    }

    @ApiOperation("查规则字典")
    @GetMapping(value = "/selectRuleDict")
    public ApiResponse<List<RuleDictRsp>> selectRuleDict() {
        List<RuleDictRsp> ruleDictRspList = ruleService.selectRuleDict();
        return new ApiResponse<>(ruleDictRspList);
    }
}
