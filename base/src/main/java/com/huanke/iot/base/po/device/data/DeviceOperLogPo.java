package com.huanke.iot.base.po.device.data;

import lombok.Data;

/**
 * 设备操作日志
 * @author haoshijing
 * @version 2018年04月10日 09:34
 **/
@Data
public class DeviceOperLogPo {
    private Integer id;
    private Integer deviceId;
    private Integer funcId;
    private String requestId;
    private String funcValue;
    private Long createTime;
    private Long responseTime;
    private Integer dealRet;
}
