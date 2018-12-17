package com.huanke.iot.gateway.io.impl;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.dao.device.DeviceCustomerRelationMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.RuleMapper;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class DeviceWarningHandler extends AbstractHandler {
    @Override
    protected String getTopicType() {
        return "warning";
    }

    @Autowired
    private DeviceCustomerRelationMapper deviceCustomerRelationMapper;

    @Autowired
    private RuleMapper ruleMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Data
    public static class Message{
        private WarningMessage datas;
    }
    @Data
    public static class WarningMessage{
        private String type;
        private String value;
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {
        Integer id = getDeviceIdFromTopic(topic);
        //设备告警1分钟冷却时间
        if(stringRedisTemplate.opsForValue().get("deviceWarning."+id)==null) {
            try {
                DeviceWarningHandler.Message message = JSON.parseObject(new String(payloads), DeviceWarningHandler.Message.class);
                DeviceWarningHandler.WarningMessage warningMessage = message.getDatas();
                if (warningMessage != null) {
                    String type = warningMessage.getType();
                    if (StringUtils.isNotEmpty(type)) {
                        DeviceCustomerRelationPo deviceCustomerRelationPo = deviceCustomerRelationMapper.selectByDeviceId(id);
                        Integer customerId = deviceCustomerRelationPo.getCustomerId();
                        String ruleId = null;//stringRedisTemplate.opsForValue().get(customerId + ".rule." + type);
                        if (ruleId == null) {
                            //没有缓存项，查询表，并添加进缓存
                            List<ProjectRule> projectRules = ruleMapper.selectByMonitorValue(customerId, null);
                            projectRules.stream().forEach(projectRule -> {
                                if (StringUtils.isNotEmpty(projectRule.getMonitorValue())) {
                                    String[] split = projectRule.getMonitorValue().split(",");
                                    for (int i = 0; i < split.length; i++) {
                                        DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(Integer.valueOf(split[i]));
                                        if (type.equals(deviceAbilityPo.getDirValue())) {
                                            createDeviceWarningJob(customerId, id, warningMessage, projectRule.getId().toString());
                                            //stringRedisTemplate.opsForValue().set(customerId + ".rule." + type, projectRule.getId().toString());
                                            //stringRedisTemplate.expire(customerId + ".rule." + type, 3, TimeUnit.HOURS);
                                            //return;
                                        }
                                    }
                                }
                            });
                            createDeviceWarningJob(customerId, id, warningMessage, null);
                            //stringRedisTemplate.opsForValue().set(customerId + ".rule." + type, "");
                            //stringRedisTemplate.expire(customerId + ".rule." + type, 3, TimeUnit.HOURS);
                        } else {
                            //有缓存，使用缓存标注的规则
                            createDeviceWarningJob(customerId, id, warningMessage, ruleId);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private void createDeviceWarningJob(Integer customerId,Integer id,WarningMessage warningMessage,String ruleId){
        ProjectJobInfo projectJobInfo = new ProjectJobInfo();
        projectJobInfo.setLinkDeviceId(id);
        projectJobInfo.setSourceType(3);
        projectJobInfo.setCustomerId(customerId);
        projectJobInfo.setFlowStatus(1);
        projectJobInfo.setDescription(warningMessage.getValue());
        projectJobInfo.setFinalTime(new Date());
        projectJobInfo.setStatus(1);
        projectJobInfo.setType(1);
        projectJobInfo.setWarnStatus(2);
        projectJobInfo.setCreateTime(new Date());
        if(ruleId!=null){
            ProjectRule projectRule = ruleMapper.selectById(Integer.valueOf(ruleId));
            projectJobInfo.setIsRule(1);
            projectJobInfo.setRuleId(Integer.valueOf(ruleId));
            projectJobInfo.setName(projectRule.getName());
            projectJobInfo.setWarnLevel(projectRule.getWarnLevel());
        }else{
            projectJobInfo.setIsRule(0);
            projectJobInfo.setName(warningMessage.getType());
            projectJobInfo.setWarnLevel(3);
        }
        jobMapper.insert(projectJobInfo);
        stringRedisTemplate.opsForValue().set("deviceWarning."+id, "1");
        stringRedisTemplate.expire("deviceWarning."+id, 60, TimeUnit.SECONDS);
    }
}
