package com.huanke.iot.manage.vo.response.device.data;

import lombok.Data;

@Data
public class DeviceSensorDataVo {
    private Integer deviceId;
    private Integer co2;
    private Integer hcho;
    private Integer pm;
    private Integer hum;
    private Integer tvoc;
    private Integer tem;
    private Long startTime;
    private Long endTime;
}
