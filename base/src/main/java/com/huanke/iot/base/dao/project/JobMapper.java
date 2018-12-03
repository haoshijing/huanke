package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import com.huanke.iot.base.resp.project.JobRspPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 * jobmapper
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface JobMapper extends BaseMapper<ProjectJobInfo> {

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<JobRspPo> selectPageList(@Param("projectJobInfo") ProjectJobInfo projectJobInfo, @Param("start") int start, @Param("limit") int limit, @Param("userId") Integer userId);

    //Boolean batchForbidden(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    Boolean batchFlow(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList, @Param("targetUserStr") String targetUserStr, @Param("flowStatus") Integer flowStatus);

    Integer selectCount(@Param("projectJobInfo") ProjectJobInfo projectJobInfo, @Param("userId") Integer userId);

    Integer selectMaintenanceCountByProjectId(@Param("projectId") Integer projectId);

    Map<Object, Object> ifExistMateria(@Param("jobId") Integer jobId);

    List<ProjectJobInfo> selectListJob(@Param("projectJobInfo") ProjectJobInfo projectJobInfo);
}