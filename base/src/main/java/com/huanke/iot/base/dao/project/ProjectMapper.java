package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectBaseInfo;
import com.huanke.iot.base.request.project.ProjectRequest;
import com.huanke.iot.base.resp.project.ProjectDictRsp;
import com.huanke.iot.base.resp.project.ProjectGroupsRsp;
import com.huanke.iot.base.resp.project.ProjectPlanCount;
import com.huanke.iot.base.resp.project.ProjectRspPo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    List<ProjectRspPo> selectPageList(@Param("projectBaseInfo") ProjectBaseInfo projectBaseInfo, @Param("start") int start, @Param("limit") int limit);

    Boolean batchForbidden(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    ProjectBaseInfo selectByProjectId(@Param("projectId") Integer projectId);

    List<ProjectDictRsp> selectProjectDict(@Param("customerId") Integer customerId);

    Boolean batchReverse(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    Integer existProjectNo(@Param("customerId") Integer customerId, @Param("projectId") Integer projectId, @Param("projectNo") String projectNo);

    List<ProjectGroupsRsp> selectGroups(@Param("valueList") List<Integer> valueList);

    Integer editIfExist(@Param("projectNo") String projectNo, @Param("projectId") Integer id);

    List<ProjectBaseInfo> getAfterTime(@Param("customerId") Integer customerId,@Param("createTime") Date createTime);

    List<ProjectPlanCount> queryOtherProjectPlanCount(@Param("customerId") Integer customerId,@Param("projectId") Integer projectId);
}