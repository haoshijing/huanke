package com.huanke.iot.base.dao.format;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.format.DeviceModelFormatPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/27 13:34
 */
public interface DeviceModelFormatMapper extends BaseMapper<DeviceModelFormatPo> {

    List<Integer> obtainAbilityIdsByJoinId(@Param("modelId") Integer modelId, @Param("formatId") Integer formatId, @Param("pageId") Integer pageId, @Param("itemId") Integer itemId);
    List<DeviceModelFormatPo> obtainModelFormatPages(@Param("modelId") Integer modelId, @Param("formatId") Integer formatId);

    DeviceModelFormatPo selectByJoinId(@Param("modelId") Integer modelId, @Param("formatId") Integer formatId, @Param("pageId") Integer pageId);
}
