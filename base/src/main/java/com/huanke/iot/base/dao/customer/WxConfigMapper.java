package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.WxConfigPo;

/**
 * h5配置信息
 */
public interface WxConfigMapper extends BaseMapper<WxConfigPo> {

    WxConfigPo selectConfigByCustomerId(Integer customerId);

}
