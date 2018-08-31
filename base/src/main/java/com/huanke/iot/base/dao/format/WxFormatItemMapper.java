package com.huanke.iot.base.dao.format;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/27 13:34
 */
public interface WxFormatItemMapper  extends BaseMapper<WxFormatItemPo> {

    List<WxFormatItemPo> selectByPageId(@Param("formatId") Integer formatId, @Param("pageId") Integer pageId);

    Integer updateStatusByPageId(WxFormatItemPo wxFormatItemPo);
}
