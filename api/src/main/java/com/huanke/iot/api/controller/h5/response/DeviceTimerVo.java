package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

@Data
public class DeviceTimerVo {
    private String name;
    private Long remainTime;
    private Integer timerType;
    private Integer status;
    private Integer id;

}
