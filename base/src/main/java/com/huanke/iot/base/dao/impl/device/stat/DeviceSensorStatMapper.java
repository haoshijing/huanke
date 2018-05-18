package com.huanke.iot.base.dao.impl.device.stat;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年05月16日 20:02
 **/
public interface DeviceSensorStatMapper extends BaseMapper<DeviceSensorStatPo>{
    List<DeviceSensorStatPo> selectData(@Param("deviceId") Integer deviceId, @Param("statrTime")Long startTimestamp,@Param("endTime") Long endTimeStamp);
}
