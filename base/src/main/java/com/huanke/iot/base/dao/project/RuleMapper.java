package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.resp.project.RuleDictRsp;
import com.huanke.iot.base.resp.project.RuleRspPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 * rulemapper
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface RuleMapper extends BaseMapper<ProjectRule> {

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<RuleRspPo> selectPageList(@Param("projectRule") ProjectRule projectRule, @Param("start") int start, @Param("limit") int limit);

    Boolean batchForbidden(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    Boolean batchReverse(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<RuleDictRsp> selectRuleDict(@Param("customerId") Integer customerId);

    List<RuleRspPo> selectAllRepaireRules(@Param("customerId") Integer customerId,@Param("typeId") Integer typeId);

    List<ProjectRule> selectByMonitorValue(@Param("customerId") Integer customerId,@Param("abilityId") Integer typeId);
}