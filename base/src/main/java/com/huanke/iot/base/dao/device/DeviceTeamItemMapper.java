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

    DeviceTeamItemPo selectByDeviceMac(String mac);

    DeviceTeamItemPo selectByJoinId(@Param("deviceId") Integer deviceId, @Param("userId") Integer userId);

    DeviceTeamItemPo selectByJoinOpenId(@Param("deviceId") Integer deviceId, @Param("userOpenId") String userOpenId);

    List<DeviceTeamItemPo> selectByUserOpenId(String openId);

    List<DeviceTeamItemPo> selectItemsByDeviceId(Integer deviceId);

    List<DeviceTeamItemPo> selectByTeamId(Integer teamId);

    Integer deleteBatch(List<DeviceTeamItemPo> deviceTeamItemPoList);

    DeviceTeamItemPo selectByUserId(Integer userId);

    int deleteItemsByDeviceId(Integer deviceId);

    Integer deleteByTeamId(Integer teamId);


    int deleteByJoinId(@Param("deviceId") Integer iDeviceId, @Param("userId") Integer userId);

    int updateStatus(@Param("deviceId") Integer deviceId, @Param("userId") Integer userId, @Param("status")Integer status);

    Integer updateBatch(List<DeviceTeamItemPo> deviceTeamItemPoList);
}
