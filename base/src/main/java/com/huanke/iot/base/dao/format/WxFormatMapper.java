package com.huanke.iot.base.dao.format;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.format.WxFormatPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/27 13:35
 */
public interface WxFormatMapper extends BaseMapper<WxFormatPo> {

    List<WxFormatPo>  selectFormatsByCustomerId(WxFormatPo wxFormatPo);

}
