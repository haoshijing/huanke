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
    private Integer operType;
    private String requestId;
    private Integer operValue;
    private Long createTime;
    private Long responeTime;
    private Integer dealRet;
}
