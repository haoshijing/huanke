package com.huanke.iot.base.po.device.data;

import lombok.Data;

/**
 * 设备传感器数据
 * @author haoshijing
 * @version 2018年04月10日 09:36
 **/
@Data
public class DeviceSensorDataPo {
    private Integer id;
    private Integer deviceId;
    private Integer index;
    private Integer pm2_5;
    private Integer co2;
    /**
     * 湿度
     */
    private Integer humidity;
    /**
     * 温度
     */
    private Integer temperature;
    /**
     * 甲醛值
     */
    private Integer tvoc;
    /**
     * 甲醛化学因子数
     */
    private Integer hcho;
    private Long createTime;
}
