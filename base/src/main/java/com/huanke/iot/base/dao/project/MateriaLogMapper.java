package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectMaterialLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 * 材料使用记录
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface MateriaLogMapper extends BaseMapper<ProjectMaterialLog> {

    List<ProjectMaterialLog> selectByMaterialInfoId(@Param("materialId") Integer materialId);
}