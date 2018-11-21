package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.dto.project.JobHistoryDataDto;
import com.huanke.iot.base.po.project.ProjectJobLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 * jobLogmapper
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface JobLogMapper extends BaseMapper<ProjectJobLog> {

    List<JobHistoryDataDto> selectByJobId(@Param("jobId") Integer jobId);

    JobHistoryDataDto selectLastByJobId(@Param("jobId") Integer jobId);
}