
package com.huanke.iot.base.dao.project;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.project.ProjectPlanInfo;
import com.huanke.iot.base.resp.project.PlanInfoRsp;
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
public interface PlanMapper extends BaseMapper<ProjectPlanInfo> {

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<PlanRspPo> selectPageList(@Param("projectPlanInfo") ProjectPlanInfo projectPlanInfo, @Param("start") int start, @Param("limit") int limit, @Param("projectName") String projectName);

    Boolean batchForbidden(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    Boolean batchReverse(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<PlanRspPo> maintenance(@Param("projectId") Integer projectId, @Param("start") int start, @Param("limit") int limit);

    Integer selectMaintenanceCount(@Param("projectId")Integer projectId);

    PlanInfoRsp  selectPlanInfoById(@Param("planId") Integer planId);

    List<ProjectPlanInfo> selectAllExist();

    ProjectPlanInfo queryByJobId(@Param("jobId") Integer jobId);


    Integer selectCount(@Param("param") ProjectPlanInfo queryBean, @Param("projectName") String projectName);
}