package com.huanke.iot.manage.vo.request.device.group;

import lombok.Data;

@Data
public class GroupQueryRequest {
    private String mac;
    private String name;
    private Integer page = 1;
    private Integer limit = 20;
}
