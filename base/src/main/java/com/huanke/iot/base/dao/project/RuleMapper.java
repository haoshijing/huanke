package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectRule;
import com.huanke.iot.base.resp.DictRspPo;
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
    List<ProjectRule> selectByType(String type);

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<DictRspPo> selectPageList(@Param("dictPo") ProjectRule projectRule, @Param("start") int start, @Param("limit") int limit);
}