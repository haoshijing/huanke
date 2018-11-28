package com.huanke.iot.job;

import com.huanke.iot.base.constant.JobFlowStatusConstants;
import com.huanke.iot.base.constant.ProjectPlanCycleTypeConstants;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.PlanMapper;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import com.huanke.iot.base.po.project.ProjectPlanInfo;
import com.huanke.iot.base.po.project.ProjectRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 描述:
 * 工程管理job
 *
 * @author onlymark
 * @create 2018-11-26 上午10:40
 */
@Repository
@Slf4j
public class ProjectJob {
    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private RuleMapper ruleMapper;

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    public void createJob() {
        List<ProjectPlanInfo> projectPlanInfoList = planMapper.selectAllExist();
        for (ProjectPlanInfo projectPlanInfo : projectPlanInfoList) {
            Integer cycleType = projectPlanInfo.getCycleType();
            Integer cycleNum = projectPlanInfo.getCycleNums();
            Date nextExecuteTime = projectPlanInfo.getNextExecuteTime();
            Integer day = projectPlanInfo.getDay();
            switch (cycleType) {
                case ProjectPlanCycleTypeConstants.CYCLE_TYPE_ONCE:
                    break;
                case ProjectPlanCycleTypeConstants.CYCLE_TYPE_MONTH:
                    executeCreateJob(projectPlanInfo, 1);

                    Calendar calMonth = Calendar.getInstance();
                    calMonth.setTime(nextExecuteTime);
                    calMonth.add(Calendar.MONTH,cycleNum);
                    if(day > calMonth.getActualMaximum(Calendar.DATE)){
                        calMonth.set(Calendar.DATE, calMonth.getActualMaximum(Calendar.DATE));
                    }else{
                        calMonth.set(Calendar.DAY_OF_MONTH, day);
                    }
                    projectPlanInfo.setNextExecuteTime(calMonth.getTime());
                    planMapper.updateById(projectPlanInfo);
                    break;
                case ProjectPlanCycleTypeConstants.CYCLE_TYPE_YEAR:
                    executeCreateJob(projectPlanInfo, 2);
                    Calendar calYear = Calendar.getInstance();
                    calYear.setTime(nextExecuteTime);
                    int month = calYear.get(Calendar.MONTH) + 1;
                    calYear.add(Calendar.YEAR,cycleNum);
                    if(month == 2){
                        if(day>28){
                            calYear.set(Calendar.DATE, calYear.getActualMaximum(Calendar.DATE));
                        }else{
                            calYear.set(Calendar.DAY_OF_MONTH, day);
                        }
                    }
                    projectPlanInfo.setNextExecuteTime(calYear.getTime());
                    planMapper.updateById(projectPlanInfo);
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 生成任务
     * @param projectPlanInfo
     * @param type
     */
    private void executeCreateJob(ProjectPlanInfo projectPlanInfo, Integer type) {
        log.info("生成任务，planId={}", projectPlanInfo.getId());
        ProjectJobInfo projectJobInfo = new ProjectJobInfo();
        projectJobInfo.setCustomerId(projectPlanInfo.getCustomerId());
        projectJobInfo.setType(projectPlanInfo.getLinkType());
        projectJobInfo.setLinkDeviceId(projectPlanInfo.getLinkDeviceId());
        projectJobInfo.setLinkProjectId(projectPlanInfo.getLinkProjectId());
        projectJobInfo.setName(projectPlanInfo.getName());
        projectJobInfo.setDescription(projectPlanInfo.getDescription());
        projectJobInfo.setIsRule(projectPlanInfo.getIsRule());
        projectJobInfo.setRuleId(projectPlanInfo.getRuleId());
        ProjectRule projectRule = ruleMapper.selectById(projectPlanInfo.getRuleId());
        projectJobInfo.setWarnLevel(projectRule.getWarnLevel());
        projectJobInfo.setSourceType(1);
        projectJobInfo.setPlanId(projectPlanInfo.getId());
        //到期日期
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, projectPlanInfo.getOverTimeDays());
        projectJobInfo.setFinalTime(calendar1.getTime());
        projectJobInfo.setEnableUsers(projectPlanInfo.getEnableUsers());
        projectJobInfo.setWarnStatus(1);
        projectJobInfo.setFlowStatus(JobFlowStatusConstants.FLOW_STATUS_CREATED);
        projectJobInfo.setCreateTime(new Date());
        jobMapper.insert(projectJobInfo);
    }
}
