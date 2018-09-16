package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;


@Data
public class DeviceBindToUserRequest {
    private String openId;
    private Integer teamId;
    private String teamName;
    private DeviceQueryRequest deviceQueryRequest;
}
