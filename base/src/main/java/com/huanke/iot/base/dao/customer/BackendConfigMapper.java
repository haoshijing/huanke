package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.BackendConfigPo;

/**
 * h5配置信息
 */
public interface BackendConfigMapper extends BaseMapper<BackendConfigPo> {

    BackendConfigPo selectConfigByCustomerId(Integer customerId);

}
