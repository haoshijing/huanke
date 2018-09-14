package com.huanke.iot.base.po.device;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class DeviceTimerPo {
    private Integer id;
    private Integer deviceId;
    private Integer userId;
    private String name;
    private Integer timerType;
    private Long executeTime;
    private Integer status = CommonConstant.STATUS_YES;
    private Integer executeRet;
    private Integer dayOfWeek;
    private Integer hour;
    private Integer minute;
    private Integer second;
    private Integer type;
    private Long createTime;
    private Long lastUpdateTime;
}
