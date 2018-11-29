package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
public interface CustomerUserMapper extends BaseMapper<CustomerUserPo>{

     void flushCache();
     CustomerUserPo selectByOpenId(String openId);
     CustomerUserPo selectByUserId(Integer userId);
     CustomerUserPo selectByMac(String mac);
     List<CustomerUserPo> selectByCustomerId(Integer customerId);
     CustomerUserPo selectCustomerByMasterUserId(Integer masterUserId);
     List selectCustomerUserCount(@Param("userYear") int userYear,@Param("customerId") Integer customerId);
     Integer updateLastVisitById(CustomerUserPo customerUserPo);
     int selectUserCountByTime(@Param("startTime")Long startTime,@Param("endTime") Long endTime,@Param("customerId") Integer customerId);
     int selectLiveUserCountByTime(@Param("startTime")Long startTime,@Param("endTime") Long endTime,@Param("customerId") Integer customerId);
     int selectUserCount(@Param("customerId") Integer customerId);


}
