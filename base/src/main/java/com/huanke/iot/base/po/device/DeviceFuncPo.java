package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * 设备功能指令
 * @author haoshijing
 * @version 2018年04月16日 12:57
 **/
@Data
public class DeviceFuncPo {
    private Integer id;
    private String name;
    private String valueRange;
    private String valueType;
    private Long createTime;
}
