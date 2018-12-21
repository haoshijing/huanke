package com.huanke.iot.manage.service.project;

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
import com.huanke.iot.manage.vo.response.device.data.WarnDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        for (JobRspPo jobRspPo : jobPoList) {
            if (jobRspPo.getIsRule() == 1) {
                jobRspPo.setWarnLevel(ruleMapper.selectById(jobRspPo.getRuleId()).getWarnLevel());
            }
        }
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
        if (StringUtils.isNotEmpty(projectJobInfo.getEnableUsers()) && projectJobInfo.getEnableUsers().indexOf(userId.toString()) < 0) {
            return "无权操作该任务，请刷新重新获取任务！";
        }
        //判断当前任务是否可以执行当前操作。
        Integer operateType = request.getOperateType();
        switch (projectJobInfo.getFlowStatus()) {
            case JobFlowStatusConstants.FLOW_STATUS_CREATED:
                if (operateType != JobFlowStatusConstants.OPERATE_TYPE_DEAL && operateType != JobFlowStatusConstants.OPERATE_TYPE_IGNORE)
                    return "错误的操作，任务状态已被更改，请刷新！";
                break;
            case JobFlowStatusConstants.FLOW_STATUS_DEALING:
                if (operateType != JobFlowStatusConstants.OPERATE_TYPE_COMMIT)
                    return "错误的操作，任务状态已被更改，请刷新！";
                break;
            case JobFlowStatusConstants.FLOW_STATUS_COMMITED:
                if (operateType != JobFlowStatusConstants.OPERATE_TYPE_PASS && operateType != JobFlowStatusConstants.OPERATE_TYPE_BACK)
                    return "错误的操作，任务状态已被更改，请刷新！";
                break;
            case JobFlowStatusConstants.FLOW_STATUS_FINISH:
            case JobFlowStatusConstants.FLOW_STATUS_IGNORED:
                return "错误的操作，任务状态已被更改，请刷新！";
        }
        List<Integer> targetUsers = request.getTargetUsers();
        if (targetUsers != null && !targetUsers.isEmpty()) {
            List<String> targetUserStrList = targetUsers.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
            String targetUserStr = String.join(",", targetUserStrList);
            projectJobInfo.setEnableUsers(targetUserStr);
        }
        int flowStatus = 0;
        switch (operateType) {
            case JobFlowStatusConstants.OPERATE_TYPE_CREATE:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_CREATED;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_DEAL:
                projectJobInfo.setEnableUsers(userId.toString());
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_DEALING;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_COMMIT:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_COMMITED;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_PASS:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_FINISH;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_BACK:
                projectJobInfo.setEnableUsers(jobLogMapper.selectLastByJobId(jobId).getCreateUser().toString());
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_DEALING;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_IGNORE:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_IGNORED;
                break;
            default:
                break;
        }
        if (flowStatus == projectJobInfo.getFlowStatus()) {
            return "错误的操作，任务状态已被更改，请刷新！";
        }
        String description = request.getDescription();
        List<Integer> valueList = request.getValueList();
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
        //任务历史记录信息
        List<JobHistoryDataDto> jobHistoryDataDtos = jobLogMapper.selectByJobId(jobId);
        List<JobDetailRsp.HistoryData> historyDataList = new ArrayList<>();
        for (JobHistoryDataDto jobHistoryDataDto : jobHistoryDataDtos) {
            JobDetailRsp.HistoryData historyData = new JobDetailRsp.HistoryData();
            historyData.setUserName(userService.getUserName(jobHistoryDataDto.getCreateUser()));
            BeanUtils.copyProperties(jobHistoryDataDto, historyData);
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
        Boolean result = null;
        Integer jobId = request.getJobId();
        Integer operateType = request.getOperateType();
        String description = request.getDescription();
        List<Integer> valueList = request.getValueList();
        List<Integer> targetUsers = request.getTargetUsers();
        List<String> targetUserStrList = targetUsers.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
        String targetUserStr = String.join(",", targetUserStrList);
        String imgListStr = String.join(",", request.getImgList());
        Integer userId = userService.getCurrentUser().getId();
        ProjectJobLog projectJobLog = new ProjectJobLog();
        int flowStatus = 0;
        //日志处理
        projectJobLog.setJobId(jobId);
        projectJobLog.setDescription(description);
        projectJobLog.setCreateUser(userId);
        projectJobLog.setImgList(imgListStr);
        projectJobLog.setCreateTime(new Date());
        projectJobLog.setOperateType(operateType);
        jobLogMapper.insert(projectJobLog);

        switch (operateType) {
            case JobFlowStatusConstants.OPERATE_TYPE_CREATE:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_CREATED;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_DEAL:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_DEALING;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_COMMIT:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_COMMITED;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_PASS:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_FINISH;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_BACK:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_DEALING;
                break;
            case JobFlowStatusConstants.OPERATE_TYPE_IGNORE:
                flowStatus = JobFlowStatusConstants.FLOW_STATUS_IGNORED;
                break;
            default:
                break;
        }

        result = result && jobMapper.batchFlow(userId, valueList, targetUserStr, flowStatus);
        return result;
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

        List<ProjectJobInfo> projectJobInfoList = jobMapper.selectWarnPageList(projectJob, start, limit, userId);

        warnJobRsp.setProjectJobInfoList(projectJobInfoList);
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
                case JobFlowStatusConstants.FLOW_STATUS_DEALING:
                    flowDealing++;
                    break;
                case JobFlowStatusConstants.FLOW_STATUS_COMMITED:
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
        WarnDataVo warnDataVo3 = new WarnDataVo("待审核", flowCommited);
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
}
