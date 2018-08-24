package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.AndroidScenePo;

import java.util.List;

/**
 * 安卓场景
 */
public interface AndroidSceneMapper extends BaseMapper<AndroidScenePo> {

    void insertBatch(List<AndroidScenePo> androidScenePoList);

    List<AndroidScenePo> selectListByConfigId(AndroidScenePo androidScenePo);

}
