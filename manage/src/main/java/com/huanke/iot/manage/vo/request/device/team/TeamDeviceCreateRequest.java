package com.huanke.iot.manage.vo.request.device.team;

import lombok.Data;

@Data
public class TeamDeviceCreateRequest {
    private String name;
    private String mac;
    private String manageName;
    //联动状态，1-联动，2-不联动
    private Integer linkAgeStatus;
}
