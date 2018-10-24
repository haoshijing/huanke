package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

@Data
public class DeviceFuncRequest {
    private Integer deviceId;
    private String funcId;
    private String value;
}
