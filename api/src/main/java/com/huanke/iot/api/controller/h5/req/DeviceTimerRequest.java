package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

@Data
public class DeviceTimerRequest {

    private String deviceId;
    private Long afterTime;
    private String name;
    //1-定时开,2-定时关
    private Integer timerType;
    private Integer userId;
}
