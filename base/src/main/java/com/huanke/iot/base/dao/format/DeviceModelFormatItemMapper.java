package com.huanke.iot.base.dao.format;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.format.DeviceModelFormatItemPo;
import com.huanke.iot.base.po.format.DeviceModelFormatPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/27 13:34
 */
public interface DeviceModelFormatItemMapper extends BaseMapper<DeviceModelFormatItemPo> {

    List<Integer> obtainAbilityIdsByJoinId(@Param("modelId") Integer modelId, @Param("formatId") Integer formatId, @Param("pageId") Integer pageId, @Param("itemId") Integer itemId);
    List<DeviceModelFormatItemPo> obtainModelFormatItems(@Param("modelFormatId") Integer modelFormatId);

    DeviceModelFormatItemPo selectByJoinId(@Param("modelFormatId") Integer modelFormatId, @Param("itemId") Integer itemId);

    Integer deleteByModelFormatId(@Param("modelFormatId") Integer modelFormatId);
    Integer updateStatusByModelFormatId(DeviceModelFormatItemPo deviceModelFormatItemPo);
}
