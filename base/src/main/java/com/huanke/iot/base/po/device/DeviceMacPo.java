package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class DeviceMacPo {
    private Integer id;
    private Integer appUserId;
    private String mac;
    private Long createTime;
}
