package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

@Data
public class DeviceShareVo {
    private String openId;
    private String nickname;
    private String avatar;
    private String deviceId;
    private String deviceName;
    private Long joinTime;
}
