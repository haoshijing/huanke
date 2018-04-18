package com.huanke.iot.base.dao.impl.device.data;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月10日 09:42
 **/
public interface DeviceOperLogMapper extends BaseMapper<DeviceOperLogPo> {
    List<DeviceOperLogPo> queryLogList(@Param("deviceId") Integer deviceId, @Param("limit") Integer limit, @Param("offset") Integer offset);

    Integer updateByRequestId(DeviceOperLogPo deviceOperLogPo);

    DeviceOperLogPo queryByRequestId(String requestId);
}
