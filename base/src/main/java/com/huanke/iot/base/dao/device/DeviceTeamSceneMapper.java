package com.huanke.iot.base.dao.device;
import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.team.DeviceTeamScenePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sixiaojun
 * @version 2018-08-15
 **/
public interface DeviceTeamSceneMapper extends BaseMapper<DeviceTeamScenePo> {
    Integer insertBatch(List<DeviceTeamScenePo> deviceTeamScenePoList);
    List<DeviceTeamScenePo> selectImgVideoList(@Param("teamId") Integer teamId);

    Integer deleteByTeamId(Integer teamId);
}
