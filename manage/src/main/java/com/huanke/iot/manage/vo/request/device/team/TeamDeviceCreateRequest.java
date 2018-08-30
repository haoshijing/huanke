package com.huanke.iot.manage.vo.request.device.team;

import lombok.Data;

@Data
public class TeamDeviceCreateRequest {
    private String name;
    private String mac;
    private String manageName;
}
