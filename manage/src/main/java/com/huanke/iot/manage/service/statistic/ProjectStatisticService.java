package com.huanke.iot.manage.service.statistic;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.ProjectMapper;
import com.huanke.iot.base.po.project.ProjectBaseInfo;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import com.huanke.iot.base.resp.project.JobRspPo;
import com.huanke.iot.base.resp.project.ProjectRspPo;
import com.huanke.iot.base.util.CommonUtil;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.util.*;

@Repository
@Slf4j
public class ProjectStatisticService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private JobMapper jobMapper;
    /**
     * 查询 地址数量
     * @return
     */
    public ApiResponse<List<ProjectRspPo.ProjectPercent>> queryProjectLocationCount(){
        Integer customerId = customerService.obtainCustomerId(false);
        ProjectBaseInfo projectBaseInfo = new ProjectBaseInfo();
        projectBaseInfo.setCustomerId(customerId);
        projectBaseInfo.setStatus(1);
        int totalProjectCount = projectMapper.selectCount(projectBaseInfo);
        List<ProjectBaseInfo> lists = projectMapper.selectList(projectBaseInfo,totalProjectCount,0);
        Map<String ,Long> locationCountMap = new HashMap<>();
        lists.stream().forEach(temp-> {
            String[] temps = new String[4];
            if (temp.getBuildAddress() != null) {
                temps = temp.getBuildAddress().split(",");
            }
            if(temps.length>=3) {
                if(StringUtils.isEmpty(temps[1])){
                    temps[1] = "市辖区";
                }
                String location = temps[0] + "-" + temps[1] + "-" + temps[2];
                if (locationCountMap.get(location) == null) {
                    locationCountMap.put(location, Long.valueOf(1));
                } else {
                    locationCountMap.put(location, locationCountMap.get(location) + 1);
                }
            }
        });
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        List<ProjectRspPo.ProjectPercent> projectPercents = new ArrayList<>();
        for (String temp : locationCountMap.keySet()){
            ProjectRspPo.ProjectPercent projectPercent = new ProjectRspPo.ProjectPercent();
            projectPercent.setBuildAddress(temp);
            projectPercent.setDistance(temp.split("-")[2]);
            projectPercent.setProjectCount(locationCountMap.get(temp));
            String percent = df.format(locationCountMap.get(temp) * 100.00 / totalProjectCount) + "%";
            projectPercent.setProjectPercent(percent);
            projectPercents.add(projectPercent);
        }
        projectPercents.sort((x, y) -> Long.compare(y.getProjectCount(), x.getProjectCount()));
        if(projectPercents.size()>5)
            projectPercents = projectPercents.subList(0,5);
        return new ApiResponse<>(projectPercents);
    }
    public List<ProjectRspPo.ProjectCountVo> queryProjectTrendCount(){
        Date time = new Date();
        time.setMonth(time.getMonth()-5);
        time.setDate(1);
        time.setHours(0);
        time.setMinutes(0);
        time.setSeconds(0);
        List<ProjectBaseInfo> sixMonths = projectMapper.getAfterTime(customerService.obtainCustomerId(false), time);
        Map<String,Long> projectCountMap = new HashMap<>();
        for(ProjectBaseInfo temp :sixMonths){
            String key = new DateTime(temp.getCreateTime()).toString("yyyy-MM");
            if(projectCountMap.get(key)==null){
                projectCountMap.put(key,Long.valueOf(1));
            }else{
                projectCountMap.put(key,projectCountMap.get(key)+1);
            }
        }
        List<ProjectRspPo.ProjectCountVo> projectCounts = new ArrayList<>();
        for(String key: projectCountMap.keySet()){
            ProjectRspPo.ProjectCountVo projectCount = new ProjectRspPo.ProjectCountVo();
            projectCount.setDate(key);
            projectCount.setProjectCount(projectCountMap.get(key));
            projectCounts.add(projectCount);
        }
        projectCounts.sort((x, y) -> Long.compare(y.getProjectCount(), x.getProjectCount()));
        return projectCounts;
    }
    public List<JobRspPo.JobCountVo> jobWarningSourceCount(){
        List<JobRspPo.JobCountVo> resp = new ArrayList<>();
        Integer customerId = customerService.obtainCustomerId(false);
        JobRspPo.JobCountVo jobCountVo0 = new JobRspPo.JobCountVo();
        ProjectJobInfo projectJobInfo = new ProjectJobInfo();
        projectJobInfo.setCustomerId(customerId);
        projectJobInfo.setSourceType(1);
        jobCountVo0.setDate("计划维保");
        jobCountVo0.setJobCount(jobMapper.selectCount(projectJobInfo,null));
        resp.add(jobCountVo0);
        JobRspPo.JobCountVo jobCountVo1 = new JobRspPo.JobCountVo();
        projectJobInfo = new ProjectJobInfo();
        projectJobInfo.setCustomerId(customerId);
        projectJobInfo.setSourceType(2);
        jobCountVo1.setDate("用户反馈");
        jobCountVo1.setJobCount(jobMapper.selectCount(projectJobInfo,null));
        resp.add(jobCountVo1);
        JobRspPo.JobCountVo jobCountVo2 = new JobRspPo.JobCountVo();
        projectJobInfo = new ProjectJobInfo();
        projectJobInfo.setCustomerId(customerId);
        projectJobInfo.setSourceType(3);
        jobCountVo2.setDate("设备告警");
        jobCountVo2.setJobCount(jobMapper.selectCount(projectJobInfo,null));
        resp.add(jobCountVo2);
        return resp;
    }
}
