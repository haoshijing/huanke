package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import org.apache.ibatis.annotations.Param;

/**
 * onlymark
 *
 * 2018/8/20
 */
public interface DeviceTeamItemMapper extends BaseMapper<DeviceTeamItemPo> {


    DeviceTeamItemPo getByDeviceId(Integer deviceId);

    int deleteByJoinId(@Param("deviceId") Integer iDeviceId, @Param("deviceId") Integer userId);
}
