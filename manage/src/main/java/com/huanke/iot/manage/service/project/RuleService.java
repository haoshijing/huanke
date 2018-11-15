package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.config.DictQueryRequest;
import com.huanke.iot.base.request.project.RuleRequest;
import com.huanke.iot.base.resp.DictRsp;
import com.huanke.iot.base.resp.DictRspPo;
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


    public DictRsp selectList(DictQueryRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        DictRsp dictRsp = new DictRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectRule projectRule = new ProjectRule();
        projectRule.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectRule);
        Integer count = ruleMapper.selectCount(projectRule);
        dictRsp.setTotalCount(count);
        dictRsp.setCurrentPage(currentPage);
        dictRsp.setCurrentCount(limit);

        List<DictRspPo> dictPoList = ruleMapper.selectPageList(projectRule, start, limit);
        dictRsp.setDictRspPoList(dictPoList);
        return dictRsp;
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
            return ruleMapper.insert(projectRule) > 0;
        } else {
            //修改
            projectRule.setUpdateTime(new Date());
            projectRule.setUpdateUser(user.getId());
            return ruleMapper.updateById(projectRule) > 0;
        }
    }

    public Boolean deleteDict(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = ruleMapper.batchDelete(userId, valueList);
        return result;
    }
}
