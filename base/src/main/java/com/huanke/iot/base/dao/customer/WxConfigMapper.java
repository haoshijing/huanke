package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.WxConfigPo;
import org.apache.ibatis.annotations.Param;

/**
 * h5配置信息
 */
public interface WxConfigMapper extends BaseMapper<WxConfigPo> {
    WxConfigPo selectConfigByCustomerId(Integer customerId);

    WxConfigPo getByJoinId(@Param("customerId") Integer customerId, @Param("password") String password);
}
