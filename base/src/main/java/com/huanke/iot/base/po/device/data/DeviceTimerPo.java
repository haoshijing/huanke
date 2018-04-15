package com.huanke.iot.base.po.device.data;

import lombok.Data;

@Data
public class DeviceTimerPo {
    private Integer id;
    private Integer deviceId;
    private Integer timingOn;
    private Integer timingOff;
    private String hepa;
    private String acticarbon;
    private Long creaetTime;
}
