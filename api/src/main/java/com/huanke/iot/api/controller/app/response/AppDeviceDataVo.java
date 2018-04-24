package com.huanke.iot.api.controller.app.response;

import lombok.Data;

@Data
public class AppDeviceDataVo {
    private Integer id;
    private String deviceName;
    private String deviceId;
    private String pm;
    private String co2;
    private String tvoc;
    private String hcho;
    private String tem;
    private String hum;
}
