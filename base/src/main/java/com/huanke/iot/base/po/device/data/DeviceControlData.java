package com.huanke.iot.base.po.device.data;

import lombok.Data;

/**
 * 设备的控制状态数据
 */
@Data
public class DeviceControlData {
    private Integer id;
    private Integer deviceId;
    private Integer funcId;
    private String funcValue;
    private Long createTime;
}
