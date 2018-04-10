package com.huanke.iot.base.po.device.data;

import lombok.Data;

@Data
public class DeviceAlarmPo {
    private Integer id;
    private Integer deviceId;
    /**
     *
     */
    private Integer index;
    private Integer type;
    private Integer value;
}

