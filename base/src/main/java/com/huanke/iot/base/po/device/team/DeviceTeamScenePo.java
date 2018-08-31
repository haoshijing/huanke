package com.huanke.iot.base.po.device.team;

import lombok.Data;

@Data
public class DeviceTeamScenePo {
    private Integer id;
    private Integer teamId;
    private String imgVideo;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
