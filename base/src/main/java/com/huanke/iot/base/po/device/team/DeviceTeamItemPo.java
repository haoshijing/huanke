package com.huanke.iot.base.po.device.team;

import lombok.Data;

/**
 * @author onlymark
 * @version 2018年08月20日
 **/
@Data
public class DeviceTeamItemPo {
    /**
     * 设备编组id
     */
    private Integer id;

    /**
     * 设备id
     */
    private Integer deviceId;

    private Integer userId;

    /**
     * 设备组id
     */
    private Integer teamId;

    private Integer userId;

    private Integer status;

    private Long createTime;

    private Long lastUpdateTime;

}