package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
public interface CustomerMapper extends BaseMapper<CustomerPo>{

     void flushCache();

     CustomerPo selectBySLD(String SLD);

     CustomerPo selectByTeamId(Integer teamId);

     CustomerPo selectByAppId(String appId);

     List<CustomerPo> selectAllCustomers(@Param("parentCustomerId")Integer parentCustomerId);

     int updateStatusById(CustomerPo customerPo);

}
