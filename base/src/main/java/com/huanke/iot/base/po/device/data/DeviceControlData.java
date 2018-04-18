package com.huanke.iot.base.po.device.data;

import lombok.Data;

/**
 * 设备的控制状态数据
 */
@Data
public class DeviceControlData {
    private Integer id;
    private Integer deviceId;
    private String funcId;
    private Integer funcValue;
    private Long createTime;
}
