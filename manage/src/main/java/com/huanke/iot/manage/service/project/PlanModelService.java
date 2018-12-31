package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.ProjectPlanCycleTypeConstants;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.PlanModelMapper;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.project.ProjectPlanModelInfo;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.PlanQueryRequest;
import com.huanke.iot.base.request.project.PlanRequest;
import com.huanke.iot.base.resp.project.PlanInfoRsp;
import com.huanke.iot.base.resp.project.PlanRsp;
import com.huanke.iot.base.resp.project.PlanRspPo;
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
 * 计划模版service
 *
 * @author onlymark
 * @create 2018-11-16 上午9:36
 */
@Slf4j
@Repository
public class PlanModelService {
    @Autowired
    private UserService userService;
    @Autowired
    private PlanModelMapper planModelMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private JobMapper jobMapper;

    private static String[] keys = {"name","description", "ruleName", "warnLevel",  "createTime","createName"};

    private static String[] texts = {"维保项", "维保内容", "规则分类", "告警级别","创建时间","创建者"};


    public PlanRsp selectList(PlanQueryRequest request) {
        PlanRsp planRsp = new PlanRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectPlanModelInfo projectPlanModel = new ProjectPlanModelInfo();
        BeanUtils.copyProperties(request, projectPlanModel);
        Integer count = planModelMapper.selectCount(projectPlanModel);
        planRsp.setTotalCount(count);
        planRsp.setCurrentPage(currentPage);
        planRsp.setCurrentCount(limit);

        List<PlanRspPo> planPoList = planModelMapper.selectPageList(projectPlanModel, start, limit);
        for (PlanRspPo planRspPo : planPoList) {
            if(planRspPo.getIsRule() == 1){
                planRspPo.setWarnLevel(ruleMapper.selectById(planRspPo.getRuleId()).getWarnLevel());
            }
        }
        planRsp.setPlanRspPoList(planPoList);
        return planRsp;
    }

    public Boolean addOrUpdate(PlanRequest request) {
        User user = userService.getCurrentUser();
        ProjectPlanModelInfo projectPlanModel = new ProjectPlanModelInfo();
        BeanUtils.copyProperties(request, projectPlanModel);
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
            projectPlanModel.setNextExecuteTime(calMonth.getTime());
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
            projectPlanModel.setNextExecuteTime(calYear.getTime());
        }
        //关联规则名称
        if(projectPlanModel.getRuleId() != null){
            ProjectRule projectRule = ruleMapper.selectById(projectPlanModel.getRuleId());
            projectPlanModel.setWarnLevel(projectRule.getWarnLevel());
        }
        if (projectPlanModel.getId() == null) {
            //添加
            projectPlanModel.setCreateTime(new Date());
            projectPlanModel.setCreateUser(user.getId());
            projectPlanModel.setStatus(CommonConstant.STATUS_YES);
            Boolean result = planModelMapper.insert(projectPlanModel) > 0;
            return result;
        } else {
            //修改
            projectPlanModel.setUpdateTime(new Date());
            projectPlanModel.setUpdateUser(user.getId());
            return planModelMapper.updateById(projectPlanModel) > 0;
        }
    }

    public Boolean deletePlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planModelMapper.batchDelete(userId, valueList);
        return result;
    }

    public Boolean forbitPlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planModelMapper.batchForbidden(userId, valueList);
        return result;
    }

    public Boolean reversePlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planModelMapper.batchReverse(userId, valueList);
        return result;
    }

    public PlanInfoRsp selectById(Integer planModelId) {
        PlanInfoRsp planInfoRsp = planModelMapper.selectPlanInfoById(planModelId);
        String enableUsers = planInfoRsp.getEnableUsers();
        if(enableUsers != null && !enableUsers.equals("")){
            List<String> enableUserListStr = Arrays.asList(enableUsers.split(","));
            List<Integer> enableUserList = enableUserListStr.stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
            planInfoRsp.setEnableUserList(enableUserList);
        }
        return planInfoRsp;
    }

    public List<PlanRspPo> queryPlanModels() {
        List<PlanRspPo> planRspList = planModelMapper.queryPlanModels();
        return planRspList;
    }
}