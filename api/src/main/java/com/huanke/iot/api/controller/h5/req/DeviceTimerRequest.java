package com.huanke.iot.api.controller.h5.req;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class DeviceTimerRequest {
    private Integer id;
    private String deviceId;
    private Long afterTime;
    private String name;
    private Integer status = CommonConstant.STATUS_YES;
    //1-定时开,2-定时关
    private Integer timerType;
    //定时类型
    private Integer type;
    private Integer userId;
    private Integer hour;
    private Integer minute;
    private Integer second;
    private Integer dayOfWeek;
}
