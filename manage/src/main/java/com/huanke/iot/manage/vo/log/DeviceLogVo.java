package com.huanke.iot.manage.vo.log;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年05月19日 14:50
 **/
@Data
public class DeviceLogVo {
    private Integer id;
    private String deviceName;
    private String operTime;
    private String value;
    private String funcType;
}
