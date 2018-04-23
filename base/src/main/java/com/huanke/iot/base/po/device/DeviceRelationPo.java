package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月23日 15:04
 **/
@Data
public class DeviceRelationPo {
    private Integer id;
    private Integer deviceId;
    private Integer masterUserId;
    private Integer joinUserId;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
