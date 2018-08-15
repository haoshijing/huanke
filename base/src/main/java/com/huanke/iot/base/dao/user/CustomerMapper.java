package com.huanke.iot.base.dao.user;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.user.CustomerPo;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
public interface CustomerMapper extends BaseMapper<CustomerPo>{

     CustomerPo selectBySLD(String SLD);
}
