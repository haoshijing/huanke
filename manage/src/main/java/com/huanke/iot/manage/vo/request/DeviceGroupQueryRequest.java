package com.huanke.iot.manage.vo.request;

import lombok.Data;

@Data
public class DeviceGroupQueryRequest {
    private String name;
    private Integer page = 1;
    private Integer limit = 20;
}
