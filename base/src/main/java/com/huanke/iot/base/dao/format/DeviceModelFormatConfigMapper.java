package com.huanke.iot.base.dao.format;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.format.DeviceModelFormatConfigPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/27 13:34
 */
public interface DeviceModelFormatConfigMapper extends BaseMapper<DeviceModelFormatConfigPo> {

    List<Integer> obtainAbilityIdsByJoinId(@Param("modelId") Integer modelId, @Param("formatId") Integer formatId, @Param("pageId") Integer pageId, @Param("itemId") Integer itemId);
}
