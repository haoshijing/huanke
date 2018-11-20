package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.AndroidSceneImgPo;

import java.util.List;

/**
 * 安卓场景图册表
 */
public interface AndroidSceneImgMapper extends BaseMapper<AndroidSceneImgPo> {

    void insertBatch(List<AndroidSceneImgPo> androidSceneImgPoList);

    List<AndroidSceneImgPo> selectListBySceneId(Integer sceneId);

}
