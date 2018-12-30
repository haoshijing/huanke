package com.huanke.iot.manage.service.project;

import com.google.common.collect.Lists;
import com.huanke.iot.base.constant.JobFlowStatusConstants;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.project.*;
import com.huanke.iot.base.dto.project.JobHistoryDataDto;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.project.*;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.*;
import com.huanke.iot.base.resp.project.*;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.vo.response.device.data.DashJobVo;
import com.huanke.iot.manage.vo.response.device.data.WarnDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述:
 * 任务service
 *
 * @author onlymark
 * @create 2018-11-16 上午9:36
 */
@Slf4j
@Repository
public class JobService {
    @Autowired
    private UserService userService;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private JobLogMapper jobLogMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private MateriaService materiaService;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private PlanMapper planMapper;


    public JobRsp selectList(JobQueryRequest request) {
        Integer userId = userService.getCurrentUser().getId();
        Integer customerId = customerService.obtainCustomerId(false);
        JobRsp jobRsp = new JobRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectJobInfo projectJob = new ProjectJobInfo();
        projectJob.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectJob);
        Integer count = jobMapper.selectCount(projectJob, userId);
        jobRsp.setTotalCount(count);
        jobRsp.setCurrentPage(currentPage);
        jobRsp.setCurrentCount(limit);

        List<JobRspPo> jobPoList = jobMapper.selectPageList(projectJob, start, limit, userId);
        jobRsp.setJobRspPoList(jobPoList);
        return jobRsp;
    }

    public Boolean addOrUpdate(JobRequest request) {
        //只在这里新建。job的详细内容不允许修改，只允许状态流转。
        User user = userService.getCurrentUser();
        ProjectJobInfo projectJobInfo = new ProjectJobInfo();
        BeanUtils.copyProperties(request, projectJobInfo);
        //特殊参数处理
        List<Integer> enableUserList = request.getEnableUserList();
        List<String> enableUserStrList = enableUserList.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
        String enableUsers = String.join(",", enableUserStrList);
        projectJobInfo.setEnableUsers(enableUsers);
        if (projectJobInfo.getId() == null) {
            //添加
            projectJobInfo.setCustomerId(customerService.obtainCustomerId(false));
            projectJobInfo.setCreateTime(new Date());
            projectJobInfo.setCreateUser(user.getId());
            return jobMapper.insert(projectJobInfo) > 0;
        } else {
            //修改
            projectJobInfo.setUpdateTime(new Date());
            projectJobInfo.setUpdateUser(user.getId());
            return jobMapper.updateById(projectJobInfo) > 0;
        }
    }

    @Transactional
    public String jobFlow(JobFlowStatusRequest request) {
        Integer jobId = request.getJobId();
        Integer userId = userService.getCurrentUser().getId();
        ProjectJobInfo projectJobInfo = jobMapper.selectById(jobId);
        if (StringUtils.isNotEmpty(projectJobInfo.getEnableUsers())) {
            if(!Arrays.asList(projectJobInfo.getEnableUsers().split(",")).contains(userId.toString()))
                return "无权操作该任务！";
        }
        //判断当前任务是否可以执行当前操作。
        String checkResult = operateCheck(request, projectJobInfo);
        if(StringUtils.isNotEmpty(checkResult)){
            return checkResult;
        }
        Integer operateType = request.getOperateType();
        List<Integer> targetUsers = request.getTargetUsers();
        String targetUserStr = null;
        if (targetUsers != null && targetUsers.size() > 0) {
            List<String> targetUserStrList = targetUsers.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
            targetUserStr = String.join(",", targetUserStrList);
        }
        String workUserStr = null;
        if (request.getWorkUsers() != null && request.getWorkUsers().size() > 0) {
            List<String> workUserStrList = request.getWorkUsers().stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
            workUserStr = String.join(",", workUserStrList);
        }
        int flowStatus = 0;
        switch (operateType) {
            case JobFlowStatusConstants.OPERATE_TYPE_CREATE:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_CREATED;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_ALLOT:
                //分配者和执行者，审核者有操作权限，分配者有查看权限
                projectJobInfo.setEnableUsers(targetUserStr);
                projectJobInfo.setViewUsers(mergerList(targetUserStr,userId.toString()));
                projectJobInfo.setWorkUsers(mergerList(workUserStr,userId.toString()));
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_ALLOTTED;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_ALLOW:
                //分配者和执行者都有操作权限和查看权限
                projectJobInfo.setEnableUsers(projectJobInfo.getWorkUsers());
                projectJobInfo.setViewUsers(mergerList(projectJobInfo.getViewUsers(),projectJobInfo.getWorkUsers()));
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_PERMIT;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_REJECT:
                //任务回归未分配状态
                projectJobInfo.setEnableUsers("");
                projectJobInfo.setViewUsers("");
                projectJobInfo.setWorkUsers("");
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_CREATED;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_COMMIT:
                //所选审批者
                projectJobInfo.setEnableUsers(targetUserStr);
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_SUBMIT;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_PASS:
                projectJobInfo.setViewUsers(mergerList(projectJobInfo.getViewUsers(),projectJobInfo.getEnableUsers()));
                projectJobInfo.setEnableUsers("");
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_FINISH;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_BACK:
                projectJobInfo.setEnableUsers(projectJobInfo.getWorkUsers());
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_PERMIT;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_IGNORE:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_IGNORED;
                break;
            default:
                break;
        }
        if (flowStatus == projectJobInfo.getFlowStatus()||flowStatus==0) {
            return "错误的操作，任务状态已被更改，请刷新！";
        }
        String description = request.getDescription();
        projectJobInfo.setFlowStatus(flowStatus);
        jobMapper.updateById(projectJobInfo);
        //日志处理
        ProjectJobLog projectJobLog = new ProjectJobLog();
        projectJobLog.setJobId(jobId);
        projectJobLog.setDescription(description);
        projectJobLog.setCreateUser(userId);
        List<String> imgList = request.getImgList();
        if (imgList != null) {
            String imgListStr = String.join(",", imgList);
            projectJobLog.setImgList(imgListStr);
        }
        projectJobLog.setCreateTime(new Date());
        projectJobLog.setOperateType(operateType);
        jobLogMapper.insert(projectJobLog);
        //处理材料
        List<MateriaUpdateRequest> materiaUpdateRequestList = request.getMateriaUpdateRequestList();
        if (materiaUpdateRequestList != null) {
            for (MateriaUpdateRequest materiaUpdateRequest : materiaUpdateRequestList) {
                Boolean result = materiaService.updateMateria(materiaUpdateRequest, projectJobLog.getId());
            }
        }
        return "操作成功！";
    }

    public String operateCheck(JobFlowStatusRequest request,ProjectJobInfo projectJobInfo){
        Integer operateType = request.getOperateType();
        List<Integer> targetUsers = request.getTargetUsers();
        String targetUserStr = null;
        if (targetUsers != null && targetUsers.size() > 0) {
            List<String> targetUserStrList = targetUsers.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
            targetUserStr = String.join(",", targetUserStrList);
        }
        String workUserStr = null;
        if (request.getWorkUsers() != null && request.getWorkUsers().size() > 0) {
        List<String> workUserStrList = request.getWorkUsers().stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
        workUserStr = String.join(",", workUserStrList);
        }
        switch (projectJobInfo.getFlowStatus()) {
            case JobFlowStatusConstants.FLOW_STATUS_CREATED:
                //待分配任务只允许分配或者忽略
                if (operateType != JobFlowStatusConstants.OPERATE_TYPE_ALLOT && operateType != JobFlowStatusConstants.OPERATE_TYPE_IGNORE)
                    return "错误的操作！请刷新任务状态。";
                if (operateType == JobFlowStatusConstants.OPERATE_TYPE_ALLOT && (StringUtils.isEmpty(workUserStr)||StringUtils.isEmpty(targetUserStr)))
                    return "指定人不能为空！";
                break;
            case JobFlowStatusConstants.FLOW_STATUS_ALLOTTED:
                //已分配任务只允许同意或拒绝
                if (operateType != JobFlowStatusConstants.OPERATE_TYPE_ALLOW && operateType != JobFlowStatusConstants.OPERATE_TYPE_REJECT)
                    return "错误的操作！请刷新任务状态。";
                break;
            case JobFlowStatusConstants.FLOW_STATUS_PERMIT:
                //已出任务只允许提交归档审核
                if (operateType != JobFlowStatusConstants.OPERATE_TYPE_COMMIT)
                    return "错误的操作！请刷新任务状态。";
                if (StringUtils.isEmpty(targetUserStr))
                    return "指定人不能为空";
                break;
            case JobFlowStatusConstants.FLOW_STATUS_SUBMIT:
                //已提交归档审核只允许通过或拒绝
                if (operateType != JobFlowStatusConstants.OPERATE_TYPE_PASS && operateType != JobFlowStatusConstants.OPERATE_TYPE_BACK)
                    return "错误的操作！请刷新任务状态。";
                break;
            case JobFlowStatusConstants.FLOW_STATUS_FINISH:
            case JobFlowStatusConstants.FLOW_STATUS_IGNORED:
                return "错误的操作！请刷新任务状态。";
        }
        return null;
    }

    public String mergerList(String a,String b){
        if(StringUtils.isEmpty(a)){
            return b;
        }
        if(StringUtils.isEmpty(b)){
            return a;
        }
        List<String> list1 = new ArrayList(Arrays.asList(a.split(",")));
        List<String> list2 = new ArrayList(Arrays.asList(b.split(",")));
        for(int i = 0 ; i<list2.size(); i++){
            if (!list1.contains(list2.get(i))){
                list1.add(list2.get(i));
            }
        }
        return String.join(",", list1);
    }
    @Transactional
    public String editJobWarnLevel(JobRequest request){
        Integer userId = userService.getCurrentUser().getId();
        ProjectJobInfo projectJobInfo = jobMapper.selectById(request.getId());
        if (projectJobInfo.getSourceType()!=2){
            return "该任务不允许修改告警等级";
        }
        projectJobInfo.setUpdateTime(new Date());
        projectJobInfo.setUpdateUser(userId);
        projectJobInfo.setWarnLevel(request.getWarnLevel());
        jobMapper.updateById(projectJobInfo);
        return "修改成功";
    }


    public JobDetailRsp selectById(Integer jobId) {
        JobDetailRsp jobDetailRsp = new JobDetailRsp();
        ProjectJobInfo projectJobInfo = jobMapper.selectById(jobId);
        BeanUtils.copyProperties(projectJobInfo, jobDetailRsp);
        Integer type = projectJobInfo.getType();//关联类型：0-不关联；1-关联设备；2-关联工程
        List<JobDetailRsp.LinkDevice> deviceList = new ArrayList<>();
        //关联设备信息
        if (type == 1 && jobDetailRsp.getLinkDeviceId() != null) {
            //关联设备
            DevicePo devicePo = deviceMapper.selectById(jobDetailRsp.getLinkDeviceId());
            JobDetailRsp.LinkDevice linkDevice = new JobDetailRsp.LinkDevice();
            deviceList.add(linkDevice);
            linkDevice.setMac(devicePo.getMac());
            linkDevice.setName(devicePo.getName());
            jobDetailRsp.setDeviceList(deviceList);
        } else if (type == 2 && jobDetailRsp.getLinkProjectId() != null) {
            //关联工程
            Integer linkProjectId = jobDetailRsp.getLinkProjectId();
            ProjectBaseInfo projectBaseInfo = projectMapper.selectById(linkProjectId);
            jobDetailRsp.setLinkProjectName(projectBaseInfo.getName());
        }
        if (projectJobInfo.getIsRule() == 1) {
            //关联规则信息
            ProjectRule projectRule = ruleMapper.selectById(projectJobInfo.getRuleId());
            jobDetailRsp.setRuleName(projectRule.getName());
            jobDetailRsp.setRuleDescription(projectRule.getDescription());
        }
        if(StringUtils.isNotEmpty(projectJobInfo.getWorkUsers()))
            jobDetailRsp.setWorkUsers(Lists.transform(Arrays.asList(projectJobInfo.getWorkUsers().split(",")),(a->{return Integer.valueOf(a);})));
        //任务历史记录信息
        List<JobHistoryDataDto> jobHistoryDataDtos = jobLogMapper.selectByJobId(jobId);
        List<JobDetailRsp.HistoryData> historyDataList = new ArrayList<>();
        for (JobHistoryDataDto jobHistoryDataDto : jobHistoryDataDtos) {
            JobDetailRsp.HistoryData historyData = new JobDetailRsp.HistoryData();
            BeanUtils.copyProperties(jobHistoryDataDto, historyData);
            historyData.setUserName(userService.getUserName(jobHistoryDataDto.getCreateUser()));
            historyData.setUserId(jobHistoryDataDto.getCreateUser());
            if (jobHistoryDataDto.getImgListStr() != null) {
                String imgListStr = jobHistoryDataDto.getImgListStr();
                List<String> imgList = Arrays.asList(imgListStr.split(","));
                historyData.setImgList(imgList);
            }
            historyDataList.add(historyData);
        }
        jobDetailRsp.setHistoryDataList(historyDataList);
        return jobDetailRsp;
    }

    @Transactional
    public Boolean flowJob(JobFlowStatusRequest request) {
        //todo
//        Boolean result = null;
//        Integer jobId = request.getJobId();
//        Integer operateType = request.getOperateType();
//        String description = request.getDescription();
//        List<Integer> valueList = request.getValueList();
//        List<Integer> targetUsers = request.getTargetUsers();
//        List<String> targetUserStrList = targetUsers.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
//        String targetUserStr = String.join(",", targetUserStrList);
//        String imgListStr = String.join(",", request.getImgList());
//        Integer userId = userService.getCurrentUser().getId();
//        ProjectJobLog projectJobLog = new ProjectJobLog();
//        int flowStatus = 0;
//        //日志处理
//        projectJobLog.setJobId(jobId);
//        projectJobLog.setDescription(description);
//        projectJobLog.setCreateUser(userId);
//        projectJobLog.setImgList(imgListStr);
//        projectJobLog.setCreateTime(new Date());
//        projectJobLog.setOperateType(operateType);
//        jobLogMapper.insert(projectJobLog);
//
//        switch (operateType) {
//            case JobFlowStatusConstants.OPERATE_TYPE_CREATE:
//                flowStatus = JobFlowStatusConstants.FLOW_STATUS_CREATED;
//                break;
//            case JobFlowStatusConstants.OPERATE_TYPE_ALLOT:
//                flowStatus = JobFlowStatusConstants.FLOW_STATUS_DEALING;
//                break;
//            case JobFlowStatusConstants.OPERATE_TYPE_COMMIT:
//                flowStatus = JobFlowStatusConstants.FLOW_STATUS_COMMITED;
//                break;
//            case JobFlowStatusConstants.OPERATE_TYPE_PASS:
//                flowStatus = JobFlowStatusConstants.FLOW_STATUS_FINISH;
//                break;
//            case JobFlowStatusConstants.OPERATE_TYPE_BACK:
//                flowStatus = JobFlowStatusConstants.FLOW_STATUS_DEALING;
//                break;
//            case JobFlowStatusConstants.OPERATE_TYPE_IGNORE:
//                flowStatus = JobFlowStatusConstants.FLOW_STATUS_IGNORED;
//                break;
//            default:
//                break;
//        }
//
//        result = result && jobMapper.batchFlow(userId, valueList, targetUserStr, flowStatus);
//        return result;
        return null;
    }


    public WarnJobRsp queryWarnJob(WarnJobRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        Integer userId = userService.getCurrentUser().getId();
        WarnJobRsp warnJobRsp = new WarnJobRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectJobInfo projectJob = new ProjectJobInfo();
        projectJob.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectJob);
        projectJob.setWarnStatus(2);
        Integer count = jobMapper.selectCount(projectJob, userId);
        warnJobRsp.setTotalCount(count);
        warnJobRsp.setCurrentPage(currentPage);
        warnJobRsp.setCurrentCount(limit);

        List<JobRspPo> jobRspPos = jobMapper.selectWarnPageList(projectJob, start, limit, userId);

        warnJobRsp.setProjectJobInfoList(jobRspPos);
        return warnJobRsp;
    }

    public List<Integer> queryAdmins(Integer jobId) {
        ProjectPlanInfo projectPlanInfo = planMapper.queryByJobId(jobId);
        if (projectPlanInfo == null) {
            throw new BusinessException("无计划信息");
        }
        String enableUsers = projectPlanInfo.getEnableUsers();
        List<Integer> adminIds = Arrays.asList(enableUsers.split(",")).stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
        return adminIds;
    }

    public List<WarnDataVo> queryWarnData() {
        Integer customerId = customerService.obtainCustomerId(false);
        List<WarnDataVo> warnDataVoList = new ArrayList<>();
        //查该客户下所有告警信息（一级查所有）
        ProjectJobInfo projectJob = new ProjectJobInfo();
        projectJob.setCustomerId(customerId);
        List<ProjectJobInfo> projectJobInfoList = jobMapper.selectWarnDataCount(projectJob);
        int flowCreated = 0;
        int flowDealing = 0;
        int flowCommited = 0;
        int flowFinish = 0;
        int flowIgnored = 0;
        for (ProjectJobInfo projectJobInfo : projectJobInfoList) {
            Integer flowStatus = projectJobInfo.getFlowStatus();
            switch (flowStatus) {
                case JobFlowStatusConstants.FLOW_STATUS_CREATED:
                    flowCreated++;
                    break;
                case JobFlowStatusConstants.FLOW_STATUS_ALLOTTED:
                case JobFlowStatusConstants.FLOW_STATUS_PERMIT:
                    flowDealing++;
                    break;
                case JobFlowStatusConstants.FLOW_STATUS_SUBMIT:
                    flowCommited++;
                    break;
                case JobFlowStatusConstants.FLOW_STATUS_FINISH:
                    flowFinish++;
                    break;
                case JobFlowStatusConstants.FLOW_STATUS_IGNORED:
                    flowIgnored++;
                    break;
                default:
                    break;
            }
        }
        WarnDataVo warnDataVo1 = new WarnDataVo("待处理", flowCreated);
        WarnDataVo warnDataVo2 = new WarnDataVo("处理中", flowDealing);
        WarnDataVo warnDataVo3 = new WarnDataVo("待归档", flowCommited);
        WarnDataVo warnDataVo4 = new WarnDataVo("已完成", flowFinish);
        WarnDataVo warnDataVo5 = new WarnDataVo("已忽略", flowIgnored);
        warnDataVoList.add(warnDataVo1);
        warnDataVoList.add(warnDataVo2);
        warnDataVoList.add(warnDataVo3);
        warnDataVoList.add(warnDataVo4);
        warnDataVoList.add(warnDataVo5);
        return warnDataVoList;
    }

    public List<MaintenanceDataVo> queryDataMaintenance() {
        Integer customerId = customerService.obtainCustomerId(false);
        List<MaintenanceDataVo> maintenanceDataVoList = new ArrayList<>();

        maintenanceDataVoList = jobMapper.queryDataMaintenance(customerId);
        return maintenanceDataVoList;
    }

    public List<DashJobVo> queryJobDash() {
        List<DashJobVo> dashJobVoList = new ArrayList<>();
        //查最近一年内的数据
        List<ProjectJobInfo> projectJobInfoList = jobMapper.queryJobDash();
        DateTime dateTime = new DateTime();
        for (int minusMonths = 11; minusMonths>=0; minusMonths--){
            DateTime currDateTime = dateTime.minusMonths(minusMonths);
            int year = currDateTime.getYear();
            int monthOfYear = currDateTime.getMonthOfYear();
            List<ProjectJobInfo> monthData;
            monthData = projectJobInfoList.stream().filter(e -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(e.getCreateTime());
                if(cal.get(Calendar.YEAR)== year && cal.get(Calendar.MONTH) + 1 == monthOfYear){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            DashJobVo dashJobVo = new DashJobVo();
            dashJobVo.setTime("" + year + "-" + monthOfYear);
            dashJobVo.setJobCount(monthData.size());
            dashJobVo.setFinishJobCount((int) monthData.stream().filter(e -> e.getFlowStatus() == 4).count());
            dashJobVoList.add(dashJobVo);
        }
        return dashJobVoList;
    }
}
