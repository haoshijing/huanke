package com.huanke.iot.base.dao.device.data;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月16日 13:22
 **/
public interface DeviceSensorDataMapper extends BaseMapper<DeviceSensorPo> {

    List<DeviceSensorPo> selectData(
            @Param("deviceId") Integer deviceId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime
    );

    Integer selectAvgData(
            @Param("deviceId") Integer deviceId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("sensorType") Integer sensorType
    );

    void clearData(long millis);
}
