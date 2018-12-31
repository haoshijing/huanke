package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.JobFlowStatusConstants;
import com.huanke.iot.base.constant.ProjectPlanCycleTypeConstants;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.PlanMapper;
import com.huanke.iot.base.dao.project.ProjectMapper;
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
import com.huanke.iot.manage.common.util.ExcelUtil;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
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
    @Autowired
    private ProjectMapper projectMapper;

    private static String[] keys = {"name","description", "ruleName", "warnLevel",  "createTime","createName"};

    private static String[] texts = {"维保项", "维保内容", "规则分类", "告警级别","创建时间","创建者"};


    public PlanRsp selectList(PlanQueryRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        PlanRsp planRsp = new PlanRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;
        String projectName = request.getProjectName();

        ProjectPlanInfo projectPlan = new ProjectPlanInfo();
        projectPlan.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectPlan);
        Integer count = planMapper.selectCount(projectPlan, projectName);
        planRsp.setTotalCount(count);
        planRsp.setCurrentPage(currentPage);
        planRsp.setCurrentCount(limit);

        List<PlanRspPo> planPoList = planMapper.selectPageList(projectPlan, start, limit, projectName);
        for (PlanRspPo planRspPo : planPoList) {
            if(planRspPo.getLinkType() == 2){
                planRspPo.setLinkProjectName(projectMapper.selectById(planRspPo.getLinkProjectId()).getName());
            }
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
        //计算下次执行时间
        Integer cycleType = request.getCycleType();
        Integer cycleNums = request.getCycleNums();
        Integer month = request.getMonth();
        Integer day = request.getDay();
        if(cycleType == ProjectPlanCycleTypeConstants.CYCLE_TYPE_MONTH){
            Calendar calMonth = Calendar.getInstance();
            calMonth.setTime(new Date());
            calMonth.add(Calendar.MONTH, cycleNums);
            if(request.getDay() > calMonth.getActualMaximum(Calendar.DATE)){
                calMonth.set(Calendar.DATE, calMonth.getActualMaximum(Calendar.DATE));
            }else{
                calMonth.set(Calendar.DAY_OF_MONTH, request.getDay());
            }
            projectPlan.setNextExecuteTime(calMonth.getTime());
        }else if(cycleType == ProjectPlanCycleTypeConstants.CYCLE_TYPE_YEAR){
            Calendar calYear = Calendar.getInstance();
            calYear.setTime(new Date());
            calYear.add(Calendar.YEAR,cycleNums);
            calYear.set(Calendar.MONTH, month);
            if(month == 2){
                if(day>28){
                    calYear.set(Calendar.DATE, calYear.getActualMaximum(Calendar.DATE));
                }else{
                    calYear.set(Calendar.DAY_OF_MONTH, day);
                }
            }
            projectPlan.setNextExecuteTime(calYear.getTime());
        }
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
            projectPlan.setStatus(CommonConstant.STATUS_YES);
            //是否立即执行
            Boolean result = planMapper.insert(projectPlan) > 0;
            excuteRight(request, projectPlan, enableUsers, isRightExcute);
            return result;

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
            projectJobInfo.setStatus(CommonConstant.STATUS_YES);
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
        if(enableUsers != null && !enableUsers.equals("")){
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
        if(planPoList != null && planPoList.size()>0) {
            for (PlanRspPo planRspPo : planPoList) {
                if (planRspPo.getIsRule() == 1) {
                    planRspPo.setWarnLevel(ruleMapper.selectById(planRspPo.getRuleId()).getWarnLevel());
                }
                if (planRspPo.getEnableUsers() != null) {
                    List<Integer> enableUserList = Arrays.asList(planRspPo.getEnableUsers().split(",")).stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
                    planRspPo.setEnableUserList(enableUserList);
                }
            }
            planRsp.setPlanRspPoList(planPoList);
        }else{
            planRsp.setPlanRspPoList(new ArrayList<>());
        }
        return planRsp;
    }

    public Boolean exportMaintenance(Integer projectId, HttpServletResponse response) throws Exception {
        Map<String, String> titleMap = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            titleMap.put(keys[i], texts[i]);
        }
        Integer count = planMapper.selectMaintenanceCount(projectId);
        List<PlanRspPo> planPoList = planMapper.maintenance(projectId, 0, count);
        for (PlanRspPo planRspPo : planPoList) {
            if(planRspPo.getIsRule() == 1){
                planRspPo.setWarnLevel(ruleMapper.selectById(planRspPo.getRuleId()).getWarnLevel());
            }
        }
        ExcelUtil<PlanRspPo> planRspPoExcelUtil = new ExcelUtil<>();
        DateTime dateTime = new DateTime();
        String time = dateTime.toString("yyyy-MM-dd");
        planRspPoExcelUtil.exportExcel("工程维保项" + time + ".xls", response, "sheet1", texts, planPoList, titleMap, planRspPoExcelUtil.EXCEl_FILE_2007);


        return true;
    }
}
