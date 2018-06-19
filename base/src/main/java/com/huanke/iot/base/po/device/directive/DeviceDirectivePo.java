package com.huanke.iot.base.po.device.directive;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年06月19日 12:57
 **/
@Data
public class DeviceDirectivePo {
    private Integer id;
    private String directiveName;
    private String directiveValue;
    private Long createTime;
    private Integer status;
}
