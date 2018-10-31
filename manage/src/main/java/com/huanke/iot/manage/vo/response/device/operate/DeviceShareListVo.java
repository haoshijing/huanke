package com.huanke.iot.manage.vo.response.device.operate;

import lombok.Data;

@Data
public class DeviceShareListVo {
    private Integer userId;
    private String openId;
    private String nickname;
    private String headImg;
    private Long joinTime;
    private Boolean status;
}
