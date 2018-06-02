package com.huanke.iot.manage.controller.device.request.type;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年06月02日 15:08
 **/
@Data
public class DeviceTypeCreateUpdateVo {
    /**
     * 类型名称
     */
    private String name;
    /**
     * 传感器列表
     */
    private String sensorList;

    /**
     * 功能列表
     */
    private String funcList;

    /**
     * 设备类型图标
     */
    private String icon;

    private Integer id;

}
