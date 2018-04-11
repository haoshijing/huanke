package com.huanke.iot.base.po.device.data;

import lombok.Data;

@Data
public class DeviceInfoPo {
    private Integer id;
    private Integer deviceId;
    private String wxInfo;
    private String mac;
    private String imei;
    private String imsi;
    private String version;
    private Long createTime;
}
