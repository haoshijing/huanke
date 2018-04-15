package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年03月27日 09:23
 **/
@Data
public class DeviceTypePo {
    private Integer id;
    private Long createTime;
    private Long lastUpdateTime;
    private String name;
    private String funcList;
    private String icon;
}
