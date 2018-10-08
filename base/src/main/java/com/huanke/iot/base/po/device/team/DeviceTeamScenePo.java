package com.huanke.iot.base.po.device.team;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class DeviceTeamScenePo {
    private Integer id;
    private Integer teamId;
    private String imgVideo;
    private Integer imgVideoMark;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
