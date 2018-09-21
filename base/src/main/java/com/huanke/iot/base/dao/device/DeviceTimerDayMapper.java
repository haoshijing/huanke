package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceTimerDayPo;

import java.util.List;

public interface DeviceTimerDayMapper extends BaseMapper<DeviceTimerDayPo>{
    List<Integer> selectDaysOfWeekByTimeId(Integer timeId);

    void deleteByTimeId(Integer timeId);
}
