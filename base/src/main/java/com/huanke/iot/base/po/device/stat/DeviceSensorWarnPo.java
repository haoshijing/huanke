package com.huanke.iot.base.po.device.stat;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceSensorWarnPo implements Serializable {
    private Integer id;
    private Integer customerId;
    private Integer temMax;
    private Integer temMin;
    private Integer humMax;
    private Integer humMin;
    private Integer pm;
    private Integer hcho;
    private Integer tvoc;
    private Integer co2;
}
