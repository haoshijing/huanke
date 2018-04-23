package com.huanke.iot.base.po.device;

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
     * 编组id
     */
    private Integer groupId;
    /**
     * 设备id
     */
    private Integer deviceId;

    private Integer userId;

    private Long createTime;

    private Long lastUpdateTime;

    private Integer status;

    /**
     * 是否是主拥有人
     */
    private Integer isMaster;
}
