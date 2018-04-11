package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年03月27日 09:22
 **/

@Data
public class DevicePo {
    /**
     * 设备id
     */
    private Integer id;
    /**
     * 设备mac地址
     */
    private String mac;
    /**
     * 设备序列号
     */
    private String devicelicence;
    private Integer deviceTypeId;
    private Integer productId;
    private Integer projectId;
    private String deviceId;
    private String deviceName;
    /**
     * 在线状态
     */
    private Integer onlineStatus;
    /**
     * 机器所在ip
     */
    private String ip;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer bindStatus;
    private Long bindTime;
}
