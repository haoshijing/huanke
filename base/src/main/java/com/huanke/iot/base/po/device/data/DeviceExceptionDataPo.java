package com.huanke.iot.base.po.device.data;

import lombok.Data;

/**
 * 异常数据
 * @author haoshijing
 * @version 2018年04月10日 09:37
 **/
@Data
public class DeviceExceptionDataPo {
    private Integer id;
    private Integer deviceId;
    private Integer exceptionType;
    private Long createTime;
}
