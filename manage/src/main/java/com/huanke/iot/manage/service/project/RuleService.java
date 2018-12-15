package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.RuleQueryRequest;
import com.huanke.iot.base.request.project.RuleRequest;
import com.huanke.iot.base.resp.project.RuleDictRsp;
import com.huanke.iot.base.resp.project.RuleRsp;
import com.huanke.iot.base.resp.project.RuleRspPo;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;


    public RuleRsp selectList(RuleQueryRequest request) {
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
        for(int i =0 ; i<rulePoList.size() ; i++ ){
            if(rulePoList.get(i).getMonitorValue()!=null) {
                rulePoList.get(i).setMonitorValues(Arrays.asList(rulePoList.get(i).getMonitorValue().split(",")));
            }
        }
        ruleRsp.setRuleRspPoList(rulePoList);
        return ruleRsp;
    }

    public Boolean addOrUpdate(RuleRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        User user = userService.getCurrentUser();
        ProjectRule projectRule = new ProjectRule();
        BeanUtils.copyProperties(request, projectRule);
        if(projectRule.getUseType() == 3 && request.getMonitorValues() != null && request.getMonitorValues().size() > 0) {
            //判断是否重复监听了同一功能项；
            List monitoredAbilitys = new ArrayList();
            ProjectRule req = new ProjectRule();
            req.setCustomerId(customerId);
            req.setUseType(3);
            req.setStatus(1);
            List<RuleRspPo> ruleDictRsps = ruleMapper.selectPageList(req,10000,0);
            for(int i = 0; i<ruleDictRsps.size(); i++){
                if(ruleDictRsps.get(i).getMonitorValue()!=null) {
                    String[] split = ruleDictRsps.get(i).getMonitorValue().split(",");
                    monitoredAbilitys.addAll(Arrays.asList(split));
                }
            }
            for(int i = 0 ;i<request.getMonitorValues().size();i++) {
                if (monitoredAbilitys.contains(request.getMonitorValues().get(i))){
                    DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(Integer.valueOf(request.getMonitorValues().get(i)));
                    throw new BusinessException("关联了已被关联的功能项："+deviceAbilityPo.getAbilityName());
                }
            }
            projectRule.setMonitorValue(String.join(",",request.getMonitorValues()));
        }
        ProjectRule oldProjectRule = null;
        if (projectRule.getId() == null) {
            //添加
            projectRule.setCustomerId(customerService.obtainCustomerId(false));
            projectRule.setCreateTime(new Date());
            projectRule.setCreateUser(user.getId());
            projectRule.setStatus(CommonConstant.STATUS_YES);
            ruleMapper.insert(projectRule);
        } else {
            oldProjectRule = ruleMapper.selectById(projectRule.getId());
            //修改
            projectRule.setUpdateTime(new Date());
            projectRule.setUpdateUser(user.getId());
            ruleMapper.updateById(projectRule);
        }
        if(projectRule.getUseType() == 3 && request.getMonitorValues() != null && request.getMonitorValues().size() > 0) {
            String oldMonitorValue = new String();
            if (oldProjectRule!=null){
                oldMonitorValue = oldProjectRule.getMonitorValue();
            }
            for(int i = 0 ; i<request.getMonitorValues().size() ; i++) {
                oldMonitorValue.replace(request.getMonitorValues().get(i),"");
                DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(Integer.valueOf(request.getMonitorValues().get(i)));
                //缓存以备getaway使用，缓存格式：客户号.rule.指令
                stringRedisTemplate.opsForValue().set(customerId + ".rule." + deviceAbilityPo.getDirValue(), projectRule.getId().toString());
            }
            String[] list = oldMonitorValue.split(",");
            for(int i = 0 ; i<list.length ; i++) {
                DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(Integer.valueOf(list[i]));
                //缓存以备getaway使用，缓存格式：客户号.rule.指令
                stringRedisTemplate.opsForValue().set(customerId + ".rule." + deviceAbilityPo.getDirValue(), "");
            }
        }
        return true;
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
    public Map<Integer, String> getEnableAbility() {
        List<DeviceAbilityPo> deviceAbilityPos = deviceAbilityMapper.selectList(new DeviceAbilityPo(), 10000, 0);
        Map<Integer, String> deviceAbilityMap = deviceAbilityPos.stream().collect(Collectors.toMap(DeviceAbilityPo::getId, a ->a.getAbilityName(), (k1, k2) -> k1));
        return deviceAbilityMap;
    }
}
