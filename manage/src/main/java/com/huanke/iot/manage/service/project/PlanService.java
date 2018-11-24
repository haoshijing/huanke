package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.JobFlowStatusConstants;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.PlanMapper;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import com.huanke.iot.base.po.project.ProjectPlanInfo;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.MaintenanceRequest;
import com.huanke.iot.base.request.project.PlanQueryRequest;
import com.huanke.iot.base.request.project.PlanRequest;
import com.huanke.iot.base.resp.project.PlanInfoRsp;
import com.huanke.iot.base.resp.project.PlanRsp;
import com.huanke.iot.base.resp.project.PlanRspPo;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 计划service
 *
 * @author onlymark
 * @create 2018-11-16 上午9:36
 */
@Slf4j
@Repository
public class PlanService {
    @Autowired
    private UserService userService;
    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private JobMapper jobMapper;


    public PlanRsp selectList(PlanQueryRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        PlanRsp planRsp = new PlanRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectPlanInfo projectPlan = new ProjectPlanInfo();
        projectPlan.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectPlan);
        Integer count = planMapper.selectCount(projectPlan);
        planRsp.setTotalCount(count);
        planRsp.setCurrentPage(currentPage);
        planRsp.setCurrentCount(limit);

        List<PlanRspPo> planPoList = planMapper.selectPageList(projectPlan, start, limit);
        for (PlanRspPo planRspPo : planPoList) {
            if(planRspPo.getIsRule() == 1){
                planRspPo.setWarnLevel(ruleMapper.selectById(planRspPo.getRuleId()).getWarnLevel());
            }
            String enableUsers = planRspPo.getEnableUsers();
            if(!enableUsers.isEmpty()){
                List<String> enableUserListStr = Arrays.asList(enableUsers.split(","));
                List<Integer> enableUserList = enableUserListStr.stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
                planRspPo.setEnableUserList(enableUserList);
            }
        }
        planRsp.setPlanRspPoList(planPoList);
        return planRsp;
    }

    public Boolean addOrUpdate(PlanRequest request) {
        User user = userService.getCurrentUser();
        ProjectPlanInfo projectPlan = new ProjectPlanInfo();
        BeanUtils.copyProperties(request, projectPlan);
        //特殊参数处理
        List<Integer> enableUserList = request.getEnableUserList();
        List<String> enableUserStrList = enableUserList.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
        String enableUsers = String.join(",", enableUserStrList);
        projectPlan.setEnableUsers(enableUsers);

        Integer isRightExcute = request.getIsRightExcute();
        //关联规则名称
        if(projectPlan.getRuleId() != null){
            ProjectRule projectRule = ruleMapper.selectById(projectPlan.getRuleId());
            projectPlan.setWarnLevel(projectRule.getWarnLevel());
        }
        if (projectPlan.getId() == null) {
            //添加
            projectPlan.setCustomerId(customerService.obtainCustomerId(false));
            projectPlan.setCreateTime(new Date());
            projectPlan.setCreateUser(user.getId());
            //是否立即执行
            excuteRight(request, projectPlan, enableUsers, isRightExcute);
            return planMapper.insert(projectPlan) > 0;
        } else {
            //修改
            projectPlan.setUpdateTime(new Date());
            projectPlan.setUpdateUser(user.getId());
            //是否立即执行
            excuteRight(request, projectPlan, enableUsers, isRightExcute);
            return planMapper.updateById(projectPlan) > 0;
        }


    }

    private void excuteRight(PlanRequest request, ProjectPlanInfo projectPlan, String enableUsers, Integer isRightExcute) {
        if(isRightExcute == 1){
            ProjectJobInfo projectJobInfo = new ProjectJobInfo();
            projectJobInfo.setCustomerId(customerService.obtainCustomerId(false));
            projectJobInfo.setType(request.getLinkType());
            projectJobInfo.setLinkDeviceId(request.getLinkDeviceId());
            projectJobInfo.setLinkProjectId(request.getLinkProjectId());
            projectJobInfo.setName(request.getName());
            projectJobInfo.setDescription(request.getDescription());
            projectJobInfo.setIsRule(request.getIsRule());
            projectJobInfo.setRuleId(request.getRuleId());
            ProjectRule projectRule = ruleMapper.selectById(request.getRuleId());
            projectJobInfo.setWarnLevel(projectRule.getWarnLevel());
            projectJobInfo.setSourceType(1);
            projectJobInfo.setPlanId(projectPlan.getId());
            //到期日期
            Calendar calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.DATE, request.getOverTimeDays());
            projectJobInfo.setFinalTime(calendar1.getTime());
            projectJobInfo.setEnableUsers(enableUsers);
            projectJobInfo.setWarnStatus(1);
            projectJobInfo.setFlowStatus(JobFlowStatusConstants.FLOW_STATUS_CREATED);
            projectJobInfo.setCreateTime(new Date());
            jobMapper.insert(projectJobInfo);
        }
    }

    public Boolean deletePlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planMapper.batchDelete(userId, valueList);
        return result;
    }

    public Boolean forbitPlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planMapper.batchForbidden(userId, valueList);
        return result;
    }

    public Boolean reversePlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planMapper.batchReverse(userId, valueList);
        return result;
    }

    public PlanInfoRsp selectById(Integer planId) {
        PlanInfoRsp planInfoRsp = planMapper.selectPlanInfoById(planId);
        String enableUsers = planInfoRsp.getEnableUsers();
        if(enableUsers != null){
            List<String> enableUserListStr = Arrays.asList(enableUsers.split(","));
            List<Integer> enableUserList = enableUserListStr.stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
            planInfoRsp.setEnableUserList(enableUserList);
        }
        return planInfoRsp;
    }

    public PlanRsp maintenance(MaintenanceRequest request) {
        Integer projectId = request.getProjectId();

        PlanRsp planRsp = new PlanRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        Integer count = planMapper.selectMaintenanceCount(projectId);
        planRsp.setTotalCount(count);
        planRsp.setCurrentPage(currentPage);
        planRsp.setCurrentCount(limit);

        List<PlanRspPo> planPoList = planMapper.maintenance(projectId, start, limit);
        for (PlanRspPo planRspPo : planPoList) {
            if(planRspPo.getIsRule() == 1){
                planRspPo.setWarnLevel(ruleMapper.selectById(planRspPo.getRuleId()).getWarnLevel());
            }
        }
        planRsp.setPlanRspPoList(planPoList);
        return planRsp;
    }
}
