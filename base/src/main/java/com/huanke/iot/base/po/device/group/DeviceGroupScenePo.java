package com.huanke.iot.base.po.device.group;

import lombok.Data;

@Data
public class DeviceGroupScenePo {
    private Integer id;
    private Integer groupId;
    private String imgVideo;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
