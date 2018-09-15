package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class DeviceTimerDayPo {
    private Integer id;
    private Integer timeId;
    private Integer dayOfWeek;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
