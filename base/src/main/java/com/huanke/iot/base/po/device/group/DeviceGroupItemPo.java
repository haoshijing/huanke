package com.huanke.iot.base.po.device.group;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:24
 **/
@Data
public class DeviceGroupItemPo {
    /**
     * 设备编组id
     */
    private Integer id;

    /**
     * 设备id
     */
    private Integer deviceId;

    /**
     * 编组id
     */
    private Integer groupId;

    private Integer status;

    private Long createTime;

    private Long lastUpdateTime;

}
