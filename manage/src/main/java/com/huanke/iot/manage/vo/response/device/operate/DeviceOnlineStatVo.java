package com.huanke.iot.manage.vo.response.device.operate;

import lombok.Data;

@Data
public class DeviceOnlineStatVo {
    private Integer totalDevice;
    private Integer onlineDevice;
    private Integer offlineDevice;
    private String onlinePercent;
}
