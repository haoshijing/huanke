package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
public interface CustomerUserMapper extends BaseMapper<CustomerUserPo>{

     CustomerUserPo selectByOpenId(String openId);
     CustomerUserPo selectByUserId(Integer userId);
     List<CustomerUserPo> selectByCustomerId(Integer customerId);
     CustomerUserPo selectCustomerByMasterUserId(Integer masterUserId);
     int updatevisitTimeByOpenId(String openId);
     List selectCustomerUserCount(int userYear);
}
