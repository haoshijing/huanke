package com.huanke.iot.base.dao.format;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import com.huanke.iot.base.po.format.WxFormatPagePo;
import com.huanke.iot.base.po.format.WxFormatPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/27 13:35
 */
public interface WxFormatPageMapper extends BaseMapper<WxFormatPagePo> {

    List<WxFormatPagePo> selectByFormatId(Integer formatId);

    Integer updateStatusByFormatId(WxFormatItemPo wxFormatItemPo);
}
