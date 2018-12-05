package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.JobFlowStatusConstants;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.project.JobLogMapper;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.ProjectMapper;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.dto.project.JobHistoryDataDto;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.project.ProjectBaseInfo;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import com.huanke.iot.base.po.project.ProjectJobLog;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.*;
import com.huanke.iot.base.resp.project.JobDetailRsp;
import com.huanke.iot.base.resp.project.JobRsp;
import com.huanke.iot.base.resp.project.JobRspPo;
import com.huanke.iot.base.resp.project.WarnJobRsp;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
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
        List<Integer> targetUsers = request.getTargetUsers();
        if (targetUsers != null && !targetUsers.isEmpty()) {
            List<String> targetUserStrList = targetUsers.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
            String targetUserStr = String.join(",", targetUserStrList);
            projectJobInfo.setEnableUsers(targetUserStr);
        }
        Integer operateType = request.getOperateType();
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
            ProjectBaseInfo projectBaseInfo = projectMapper.selectById(linkProjectId) ;
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
}
