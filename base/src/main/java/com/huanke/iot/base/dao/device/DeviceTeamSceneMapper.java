package com.huanke.iot.base.dao.device;
import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.team.DeviceTeamScenePo;

import java.util.List;

/**
 * @author sixiaojun
 * @version 2018-08-15
 **/
public interface DeviceTeamSceneMapper extends BaseMapper<DeviceTeamScenePo> {
    Integer insertBatch(List<DeviceTeamScenePo> deviceTeamScenePoList);
}
