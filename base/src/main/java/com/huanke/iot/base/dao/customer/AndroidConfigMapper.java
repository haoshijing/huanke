package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.AndroidConfigPo;

/**
 * 安卓配置
 */
public interface AndroidConfigMapper extends BaseMapper<AndroidConfigPo> {

    AndroidConfigPo selectConfigByCustomerId(Integer customerId);

}
