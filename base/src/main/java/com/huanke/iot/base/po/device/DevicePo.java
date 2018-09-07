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



    //设备序列号
    private String imei;
    private String imsi;
    private String saNo;

    //设备 类型、型号
    private Integer typeId;
    private Integer modelId;
    private String productId;
    /**
     * 设备微信备案号
     */
    private String wxDeviceId;
    private String wxDevicelicence;
    private String wxQrticket;

    private Long bindTime;
    //绑定状态
    private Integer bindStatus;
    //在线状态
    private Integer onlineStatus;
    //启用状态
    private Integer enableStatus;
    //工作状态 或 租赁状态
    private Integer workStatus;
    //设备位置
    private String location;

    //设备 主从状态 1-主设备 ；2-从设备
    private Integer hostStatus;
    //从设备 的主设备id
    private Integer hostDeviceId;
    //从设备编号
    private String childId;

    /**
     * 机器所在ip
     */
    private String ip;
    private String speedConfig;
    private String version;
    private int status;
    private String hardVersion;
    private String communicationVersion;
    private String softVersion;
    private Long birthTime;
    private Long createTime;
    private Long lastUpdateTime;

}