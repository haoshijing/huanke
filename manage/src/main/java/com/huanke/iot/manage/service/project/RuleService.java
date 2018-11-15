package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.RuleRequest;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

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
}
