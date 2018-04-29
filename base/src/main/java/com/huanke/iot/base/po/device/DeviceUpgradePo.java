package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class DeviceUpgradePo {
    private Integer id;
    private Integer deviceId;
    private String fileName;
    private Integer fileSize;
    private String md5;
    private Long createTime;
    private Long lastUpdateTime;
}
