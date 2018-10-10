package com.huanke.iot.manage.controller.device.response;

import lombok.Data;

@Data
public class DeviceOperLogVo {
    /**
     * 操作功能
     */
    private String funcId;

    /**
     * 设置的值
     */
    private String value;
    /**
     * 操作时间
     */
    private Long createTime;
    /**
     * 操作来源
     */
    private Integer source;

    /**
     * 操作来源
     */
    private String operator;

    /**
     * 设备返回时间
     */
    private Long responseTime;

    /**
     * 响应数据
     */

    private String responseData;

}
