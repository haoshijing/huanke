package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;


@Data
public class DeviceBindToUserRequest {
    private Integer userId;
    private String openId;
    private Integer teamId;
    private DeviceQueryRequest deviceQueryRequest;
}
