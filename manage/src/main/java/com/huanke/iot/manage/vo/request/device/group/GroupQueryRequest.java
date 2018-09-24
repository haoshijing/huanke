package com.huanke.iot.manage.vo.request.device.group;

import lombok.Data;

@Data
public class GroupQueryRequest {
    private Integer id;
    private String name;
    private Integer customerId;
    private String createLocation;
    private Integer page = 1;
    private Integer limit = 20;
}
