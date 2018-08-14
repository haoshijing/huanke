package com.huanke.iot.base.dao.impl.user;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.user.CustomerPo;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
public interface CustomerUserMapper extends BaseMapper<CustomerPo>{

     CustomerPo selectByOpenId(String openId);
     int updatevisitTimeByOpenId(String openId);
}
