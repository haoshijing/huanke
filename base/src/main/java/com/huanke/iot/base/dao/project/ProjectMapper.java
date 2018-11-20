package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectBaseInfo;
import com.huanke.iot.base.resp.project.PlanRspPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 * rulemapper
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface ProjectMapper extends BaseMapper<ProjectBaseInfo> {

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<PlanRspPo> selectPageList(@Param("projectBaseInfo") ProjectBaseInfo projectBaseInfo, @Param("start") int start, @Param("limit") int limit);

    Boolean batchForbidden(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);
}