package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectImplementLog;
import com.huanke.iot.base.resp.project.ImplementRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 * rulemapper
 *
 * @author onlymark
 * @create 2018-11-14 下午5:59
 */
public interface ImplMapper extends BaseMapper<ProjectImplementLog> {

    List<ImplementRsp> selectByProjectId(@Param("projectId") Integer projectId);
}