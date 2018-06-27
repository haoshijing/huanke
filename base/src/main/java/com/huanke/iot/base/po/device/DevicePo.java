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
    private String name;
    private Integer isBind;
    /**
     * 在线状态
     */
    private Integer onlineStatus;
    /**
     * 机器所在ip
     */
    private String ip;
    private String speedConfig;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer bindStatus;
    private Long bindTime;
    private String location;
    private String mode;
    /**
     * 设备所属公众号
     */
    private Integer publicId;
    private String wxProductId = "";
}
