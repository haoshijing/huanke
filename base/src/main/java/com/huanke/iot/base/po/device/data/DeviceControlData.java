package com.huanke.iot.base.po.device.data;

import lombok.Data;

/**
 * 设备的控制状态数据
 */
@Data
public class DeviceControlData {
    private Integer id;
    private Integer deviceId;
    private Integer mode;
    private Integer devicelock;
    private Integer childlock;
    private Integer anion;
    private Integer uvl;
    private Integer heater;
    private String fan;
    private String valve;
    private Long createTime;
}
