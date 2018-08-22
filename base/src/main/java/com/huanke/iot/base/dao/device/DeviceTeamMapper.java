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

    DeviceTeamPo queryByName(DeviceTeamPo queryPo);

    int updateDeviceGroupId(@Param("userId") Integer userId,
                            @Param("newTeamId") int newTeamId,
                            @Param("deviceId") Integer deviceId);

    Integer updateTeamStatus(@Param("userId") Integer userId,@Param("teamId") Integer teamId, @Param("status") Integer status);

    int updateDeviceTeamItem(@Param("userId") Integer userId,
                              @Param("currentTeamId") Integer currentTeamId,
                              @Param("newTeamId") int newTeamId);
}
