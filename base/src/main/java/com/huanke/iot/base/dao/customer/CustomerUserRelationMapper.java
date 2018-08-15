package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserRelationPo;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
public interface CustomerUserRelationMapper extends BaseMapper<CustomerUserRelationPo>{

     CustomerPo selectBySLD(String SLD);
}
