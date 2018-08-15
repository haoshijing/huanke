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
    private String name;

    /**
     * 设备mac地址
     */
    private String mac;

    /**
     * 设备微信备案号
     */
    private String deviceId;
    private String devicelicence;

    //设备序列号
    private String imei;
    private String imsi;
    private String saNo;

    private Integer typeId;
    private Integer productId;

    private Long bindTime;
    //绑定状态
    private Integer bindStatus;
    //在线状态
    private Integer onlineStatus;
    //启用状态
    private Integer enableStatus;
    //工作状态 或 租赁状态
    private Integer workStatus;


    /**
     * 机器所在ip
     */
    private String ip;
    private String speedConfig;
    private String version;
    private Long createTime;
    private Long lastUpdateTime;

}
