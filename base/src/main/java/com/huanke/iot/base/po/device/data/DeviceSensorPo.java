package com.huanke.iot.base.po.device.data;

import lombok.Data;

/**
 * 传感器数据
 */
@Data
public class DeviceSensorPo {
    private Integer id;
    private String sensorType;
    private Integer sensorValue;
    private Integer deviceId;
    private Long createTime;
}
