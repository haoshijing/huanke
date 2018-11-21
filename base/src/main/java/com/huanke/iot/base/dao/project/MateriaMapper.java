package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectMaterialInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 * rulemapper
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface MateriaMapper extends BaseMapper<ProjectMaterialInfo> {
    List<Integer> selectExistIds(@Param("projectId") Integer projectId);

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<ProjectMaterialInfo> selectMaterialInfoByProjectId(Integer projectId);
}