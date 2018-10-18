package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

@Data
public class DeviceDataQueryRequest {
    private Integer deviceId;
    private Integer page = 1;
    private Integer limit = 20;
}
