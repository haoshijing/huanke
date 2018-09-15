package com.huanke.iot.api.controller.h5.response;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

@Data
public class DeviceTimerVo {
    private Integer id;
    private String name;
    private Long remainTime;
    private Integer timerType;
    private Integer status = CommonConstant.STATUS_YES;
    private Integer type;
    private List<Integer> daysOfWeek;
    private Integer hour;
    private Integer minute;
    private Integer second;
}
