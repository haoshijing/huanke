package com.huanke.iot.job.bean;

import lombok.Data;

/**
 * 描述:
 * 设备导出传感器数据Bean
 *
 * @author onlymark
 * @create 2019-05-14 下午1:39
 */
@Data
public class DeviceExportDataBean {
    private String mac;
    private String funcName;
    private Integer sensorValue;
    private String unit;
    private String time;

    public DeviceExportDataBean(String mac, String funcName, Integer sensorValue, String unit, String time) {
        this.mac = mac;
        this.funcName = funcName;
        this.sensorValue = sensorValue;
        this.unit = unit;
        this.time = time;
    }
}
