package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class DeviceIdPoolPo {
    private Integer id;
    private Integer customerId;
    private String deviceId;
    private String deviceLicense;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
