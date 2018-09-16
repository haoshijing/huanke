package com.huanke.iot.manage.vo.request.device.team;

import lombok.Data;

@Data
public class TeamListQueryRequest {
    private String name;
    private Integer createUserId;
    private Integer masterUserId;
    private Integer customerId;
    private Integer status;
    private Integer teamType;
    private Integer page = 1;
    private Integer limit = 20;
}
