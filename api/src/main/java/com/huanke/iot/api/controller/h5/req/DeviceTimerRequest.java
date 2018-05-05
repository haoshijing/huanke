package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

@Data
public class DeviceTimerRequest {
    private Integer id;
    private String deviceId;
    private Long afterTime;
    private String name;
    private Integer status;
    //1-定时开,2-定时关
    private Integer timerType;
    private Integer userId;
}
