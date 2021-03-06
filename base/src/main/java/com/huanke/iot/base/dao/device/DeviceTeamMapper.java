package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * onlymark
 *
 * 2018/8/20
 */
public interface DeviceTeamMapper  extends BaseMapper<DeviceTeamPo> {

    List<DeviceTeamItemPo> queryTeamItems(DeviceTeamItemPo queryDeviceTeamItem);

    List<DeviceTeamPo> selectTeamList(DeviceTeamPo deviceTeamPo);

    Integer queryItemCount(DeviceTeamItemPo queryItemPo);

    DeviceTeamPo queryByName(DeviceTeamPo queryPo);

    List<DeviceTeamPo> selectByUserOpenId(@Param("openId") String openId,@Param("customerId") Integer customerId);



    Integer updateTeamStatus(@Param("userId") Integer userId,@Param("teamId") Integer teamId, @Param("status") Integer status);

    Integer updateStatusById(@Param("id") Integer id,@Param("status") Integer status);

    int updateDeviceTeamItem(@Param("userId") Integer userId,
                              @Param("currentTeamId") Integer currentTeamId,
                              @Param("newTeamId") int newTeamId);

    Integer queryTeamCount(@Param("userId") Integer userId, @Param("teamName") String teamName);

    List<DeviceTeamPo> selectByMasterUserId(Integer userId);

    Integer verifyTeam(@Param("userId") Integer userId, @Param("teamId") Integer teamId);
}
