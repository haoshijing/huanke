package com.huanke.iot.api.service.user;

import com.huanke.iot.api.controller.h5.req.UserFeedbackRequest;
import com.huanke.iot.api.controller.h5.req.UserRepairInfo;
import com.huanke.iot.api.controller.h5.response.RepairInfoLogVo;
import com.huanke.iot.api.controller.h5.response.RuleInfoVo;
import com.huanke.iot.base.dao.DictMapper;
import com.huanke.iot.base.dao.customer.UserFeedbackMapper;
import com.huanke.iot.base.dao.project.JobLogMapper;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.customer.UserFeedbackPo;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import com.huanke.iot.base.po.project.ProjectJobLog;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.resp.QueryDictRsp;
import com.huanke.iot.base.resp.project.RuleRsp;
import com.huanke.iot.base.resp.project.RuleRspPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class UserFeedbackService {
    @Autowired
    private UserFeedbackMapper userFeedbackMapper;
    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private JobLogMapper jobLogMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Transactional
    public String saveUserFeedback(Integer userId , UserFeedbackRequest userFeedbackRequest){
        if(stringRedisTemplate.opsForValue().get("userFeedback."+userId)==null){
            userFeedbackRequest.setFeedbackInfo(filter(userFeedbackRequest.getFeedbackInfo()));
            UserFeedbackPo userFeedbackPo = new UserFeedbackPo();
            userFeedbackPo.setUserId(userId);
            userFeedbackPo.setDeviceId(userFeedbackRequest.getDeviceId());
            userFeedbackPo.setFeedbackInfo(userFeedbackRequest.getFeedbackInfo());
            userFeedbackPo.setCreateTime(System.currentTimeMillis());
            userFeedbackMapper.insert(userFeedbackPo);
            stringRedisTemplate.opsForValue().set("userFeedback."+userId, "1");
            stringRedisTemplate.expire("userFeedback."+userId, 60, TimeUnit.SECONDS);
            return "感谢您提供的宝贵意见！";
        }else{
            return "您的手机卡了，请稍后重新填写，感谢您的支持！";
        }
    }

    public List<RuleInfoVo> getRuleInfo(Integer customerId){
        List<QueryDictRsp> projectTypeRules = dictMapper.queryDict(customerId, "planning");
        if (projectTypeRules!= null && projectTypeRules.size()>0) {
            List<RuleInfoVo> ruleInfoVos = new ArrayList<>();
            for (QueryDictRsp temp : projectTypeRules) {
                List<RuleRspPo> ruleRspPos = ruleMapper.selectAllRepaireRules(customerId, temp.getId());
                if (ruleRspPos != null && ruleRspPos.size() > 0) {
                    RuleInfoVo ruleInfoVo = new RuleInfoVo();
                    ruleInfoVo.setDictId(temp.getId());
                    ruleInfoVo.setDictName(temp.getLabel());
                    List<RuleInfoVo.RuleInfo> rules = new ArrayList<>();
                    for (RuleRspPo ruleRsp : ruleRspPos) {
                        RuleInfoVo.RuleInfo rule = new RuleInfoVo.RuleInfo();
                        BeanUtils.copyProperties(ruleRsp, rule);
                        rules.add(rule);
                    }
                    ruleInfoVo.setRules(rules);
                    ruleInfoVos.add(ruleInfoVo);
                }
            }
            return ruleInfoVos;
        }
        return null;
    }
    @Transactional
    public String addRepairInfo(UserRepairInfo userRepairInfo,Integer userId,Integer custId){
        ProjectJobInfo projectJob = new ProjectJobInfo();
        projectJob.setCustomerId(custId);
        projectJob.setReportCustUserId(userId);
        projectJob.setType(1);
        projectJob.setLinkDeviceId(userRepairInfo.getDeviceId());
        if(userRepairInfo.getRuleId() != null && userRepairInfo.getRuleId() > 0 ) {
            ProjectRule projectRule = ruleMapper.selectById(userRepairInfo.getRuleId());
            projectJob.setName(projectRule.getName());
            projectJob.setIsRule(1);
            projectJob.setRuleId(userRepairInfo.getRuleId());
            projectJob.setWarnLevel(projectRule.getWarnLevel());
        }else {
            projectJob.setIsRule(0);
            projectJob.setName("用户反馈");
            projectJob.setWarnLevel(3);
        }
        projectJob.setFlowStatus(1);
        projectJob.setDescription(userRepairInfo.getDescription());
        projectJob.setSourceType(2);
        Date finaltime = new Date();
        finaltime.setDate(finaltime.getDate()+1);
        projectJob.setFinalTime(finaltime);
        projectJob.setWarnStatus(2);
        projectJob.setStatus(1);
        projectJob.setCreateTime(new Date());
        jobMapper.insert(projectJob);
        ProjectJobLog projectJobLog = new ProjectJobLog();
        projectJobLog.setJobId(projectJob.getId());
        projectJobLog.setDescription(userRepairInfo.getDescription());
        projectJobLog.setOperateType(1);
        projectJobLog.setCreateTime(new Date());
        jobLogMapper.insert(projectJobLog);
        return "反馈成功！";
    }

    public List<RepairInfoLogVo> getRepairInfoLog(Integer userId,Integer custId,Integer deviceId){
        ProjectJobInfo projectJobInfo = new ProjectJobInfo();
        projectJobInfo.setCustomerId(custId);
        projectJobInfo.setReportCustUserId(userId);
        projectJobInfo.setLinkDeviceId(deviceId);
        List<ProjectJobInfo> projectJobInfos = jobMapper.selectListJob(projectJobInfo);
        List<RepairInfoLogVo> repairInfoLogVos = new ArrayList<>();
        if ( projectJobInfos != null && projectJobInfos.size() > 0){
            projectJobInfos.stream().forEach(temp->{
                RepairInfoLogVo repairInfoLogVo = new RepairInfoLogVo();
                repairInfoLogVo.setName(temp.getName());
                repairInfoLogVo.setCreateTime(temp.getCreateTime());
                repairInfoLogVo.setDescription(temp.getDescription());
                switch (temp.getFlowStatus()){
                    case 1:
                        repairInfoLogVo.setStatus("待处理");
                        break;
                    case 2:
                    case 3:
                        repairInfoLogVo.setStatus("处理中");
                        break;
                    case 4:
                    case 5:
                        repairInfoLogVo.setStatus("已结束");
                        break;
                }
                repairInfoLogVos.add(repairInfoLogVo);
            });
        }
        return repairInfoLogVos;
    }


    public String filter(String input){
        return input;
    }
}
