package com.huanke.iot.manage.response;

import lombok.Data;

@Data
public class DeviceTypeVo {
    private Integer id;
    private String name;
    private String icon;
    private String funcList;
    /**
     * 传感器列表
     */
    private String sensorList;

}
