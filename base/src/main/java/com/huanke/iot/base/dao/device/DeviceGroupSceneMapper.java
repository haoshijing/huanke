package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.group.DeviceGroupScenePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceGroupSceneMapper extends BaseMapper<DeviceGroupScenePo> {
    Integer insertBatch(List<DeviceGroupScenePo> deviceGroupScenePoList);


    List<DeviceGroupScenePo> selectImgVideoList(@Param("groupId") Integer groupId);

    Integer deleteByGroupId(Integer groupId);

    Integer deleteBatch(List<DeviceGroupScenePo> deviceGroupScenePoList);
}
