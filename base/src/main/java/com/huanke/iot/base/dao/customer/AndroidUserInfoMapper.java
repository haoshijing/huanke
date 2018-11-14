package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.AndroidUserInfoPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 安卓配置
 */
public interface AndroidUserInfoMapper extends BaseMapper<AndroidUserInfoPo> {

    AndroidUserInfoPo selectByCustomerAndImei(@Param("customerId")Integer customerId,@Param("imei")String imei);
}
