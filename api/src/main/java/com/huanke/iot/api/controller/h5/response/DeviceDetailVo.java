package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月10日 10:21
 **/
@Data
public class DeviceDetailVo {
    private Integer childlock;
    private Integer pm;
    private Integer temperature;
    private Integer humidity;
    private Integer co2;
    /**
     * 甲醛值
     */
    private Integer tvoc;
    /**
     * 甲醛化学因子数
     */
    private Integer hcho;
}
