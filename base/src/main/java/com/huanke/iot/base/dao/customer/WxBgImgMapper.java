package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.WxBgImgPo;

import java.util.List;

/**
 * 安卓场景图册表
 */
public interface WxBgImgMapper extends BaseMapper<WxBgImgPo> {

    List<WxBgImgPo> selectListByConfigId(WxBgImgPo wxBgImgPo);

}
