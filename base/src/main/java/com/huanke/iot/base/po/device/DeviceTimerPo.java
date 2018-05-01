package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class DeviceTimerPo {
    private Integer id;
    private Integer deviceId;
    private Integer userId;
    private String name;
    private Integer timerType;
    private Long executeTime;
    private Integer status;
    private Integer executeRet;
    private Long createTime;
    private Long lastUpdateTime;
}
