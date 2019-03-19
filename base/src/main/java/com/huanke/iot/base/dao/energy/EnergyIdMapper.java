package com.huanke.iot.base.dao.energy;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.energy.EnergyIdPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnergyIdMapper extends BaseMapper<EnergyIdPo> {
    List<EnergyIdPo> selectEnergyIdsByPageId(@Param("pageId") String pageId);
}