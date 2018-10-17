package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

@Data
public class DeviceShareVo {
    private Integer userId;
    private String openId;
    private String nickname;
    private String headImg;
    private Long joinTime;
    private Boolean status;
}
