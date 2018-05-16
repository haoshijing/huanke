package com.huanke.iot.base.po.device.stat;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年05月16日 20:00
 **/
@Data
public class DeviceSensorStatPo {
    private Integer id;
    private Integer deviceId;
    private Integer co2;
    private Integer hum;
    private Integer tem;
    private Integer pm;
    private Integer tvoc;
    private Integer hcho;
    private Long startTime;
    private Long endTime;
    private Long insertTime;
}
