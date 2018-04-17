package com.huanke.iot.api.controller.h5;

import lombok.Data;

@Data
public class DeviceFuncVo {
    private String deviceId;
    private Integer funcId;
    private String value;
}
