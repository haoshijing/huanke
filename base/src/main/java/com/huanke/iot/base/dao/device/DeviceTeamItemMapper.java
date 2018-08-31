package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * onlymark
 *
 * 2018/8/20
 */
public interface DeviceTeamItemMapper extends BaseMapper<DeviceTeamItemPo> {

    Integer insertBatch(List<DeviceTeamItemPo> deviceTeamItemPoList);

    DeviceTeamItemPo selectByDeviceId(Integer deviceId);

    DeviceTeamItemPo selectByTeamId(Integer teamId);

    int deleteByJoinId(@Param("deviceId") Integer iDeviceId, @Param("deviceId") Integer userId);

    void updateStatus(@Param("deviceId") Integer deviceId, @Param("userId") Integer userId, @Param("status")Integer status);
}
