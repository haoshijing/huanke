package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.config.DictQueryRequest;
import com.huanke.iot.base.request.project.RuleRequest;
import com.huanke.iot.base.resp.project.RuleDictRsp;
import com.huanke.iot.base.resp.project.RuleRsp;
import com.huanke.iot.base.resp.project.RuleRspPo;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 * 规则service
 *
 * @author onlymark
 * @create 2018-11-14 下午5:56
 */
@Slf4j
@Repository
public class RuleService {
    @Autowired
    private UserService userService;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private CustomerService customerService;


    public RuleRsp selectList(DictQueryRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        RuleRsp ruleRsp = new RuleRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectRule projectRule = new ProjectRule();
        projectRule.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectRule);
        Integer count = ruleMapper.selectCount(projectRule);
        ruleRsp.setTotalCount(count);
        ruleRsp.setCurrentPage(currentPage);
        ruleRsp.setCurrentCount(limit);

        List<RuleRspPo> rulePoList = ruleMapper.selectPageList(projectRule, start, limit);
        ruleRsp.setRuleRspPoList(rulePoList);
        return ruleRsp;
    }

    public Boolean addOrUpdate(RuleRequest request) {
        User user = userService.getCurrentUser();
        ProjectRule projectRule = new ProjectRule();
        BeanUtils.copyProperties(request, projectRule);
        if (projectRule.getId() == null) {
            //添加
            projectRule.setCustomerId(customerService.obtainCustomerId(false));
            projectRule.setCreateTime(new Date());
            projectRule.setCreateUser(user.getId());
            projectRule.setStatus(CommonConstant.STATUS_YES);
            return ruleMapper.insert(projectRule) > 0;
        } else {
            //修改
            projectRule.setUpdateTime(new Date());
            projectRule.setUpdateUser(user.getId());
            return ruleMapper.updateById(projectRule) > 0;
        }
    }

    public Boolean reverseRule(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = ruleMapper.batchReverse(userId, valueList);
        return result;
    }

    public Boolean deleteRule(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = ruleMapper.batchDelete(userId, valueList);
        return result;
    }

    public Boolean forbitRule(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = ruleMapper.batchForbidden(userId, valueList);
        return result;
    }

    public List<RuleDictRsp> selectRuleDict() {
        Integer customerId = customerService.obtainCustomerId(false);
        return ruleMapper.selectRuleDict(customerId);
    }
}
