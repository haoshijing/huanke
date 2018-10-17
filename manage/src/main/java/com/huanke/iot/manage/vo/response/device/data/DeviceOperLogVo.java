package com.huanke.iot.manage.vo.response.device.data;

import lombok.Data;

@Data
public class DeviceOperLogVo {
    /**
     * 操作功能
     */
    private String funcId;

    private String funcName;

    private String funcRemark;

    /**
     * 设置的值
     */
    private String funcValue;
    /**
     * 操作时间
     */
    private Long operateTime;
    /**
     * 操作人id
     */
    private Integer operUserId;

    private String operName;

    /**
     * 操作来源
     */
    private Integer operType;

    /**
     * 设备返回时间
     */
    private Long responseTime;

    /**
     * 响应数据
     */

    private String retMsg;

}
