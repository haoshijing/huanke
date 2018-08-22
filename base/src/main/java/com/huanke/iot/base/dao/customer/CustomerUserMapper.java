package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
public interface CustomerUserMapper extends BaseMapper<CustomerUserPo>{

     CustomerUserPo selectByOpenId(String openId);
     int updatevisitTimeByOpenId(String openId);
}
