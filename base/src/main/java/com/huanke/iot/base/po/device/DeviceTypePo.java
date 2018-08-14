package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年03月27日 09:23
 **/
@Data
public class DeviceTypePo {
    private Integer id;
    private String name;
    private String typeNo;//类型编号
    private String icon;//缩略图
    private String remark;
    private Long createTime;
    private Long lastUpdateTime;
}
