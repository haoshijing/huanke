package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class DeviceIdPoolPo {
    private Integer id;
    private String deviceId;
    private Integer publicId;
    private String devicelicence;
    private Long createTime;
}
