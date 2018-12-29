package com.huanke.iot.base.dao.device.stat;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.stat.DeviceSensorWarnPo;
import org.apache.ibatis.annotations.Param;



/**
 * @author liuxiaoyu
 * @version 2018年12月29日 20:02
 **/
public interface DeviceSensorWarnMapper extends BaseMapper<DeviceSensorWarnPo> {
    DeviceSensorWarnPo selectByCustomerId(@Param("customerId") Integer customerId);
}
