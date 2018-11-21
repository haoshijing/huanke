package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectExtraDevice;
import com.huanke.iot.base.request.project.ProjectRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 * rulemapper
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface ExtraDeviceMapper extends BaseMapper<ProjectExtraDevice> {

    List<Integer> selectExistIds(@Param("projectId") Integer projectId);

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    ProjectRequest selectByProjectId(Integer projectId);

    List<ProjectExtraDevice> selectExtraDeviceByProjectId(@Param("projectId") Integer projectId);
}