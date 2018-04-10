package com.huanke.iot.base.po.device.data;

import lombok.Data;

@Data
public class DeviceLocationDataPo {
    private Integer id;
    private Integer deviceId;
    private String wifi;
    private String grps;
    private String gps;
    private String blutooth;
    private String extFields;
}
