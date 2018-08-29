package com.huanke.iot.manage.vo.response.device;

import lombok.Data;

@Data
public class DeviceListVo {
    /**
     *设备名
     */
    private String name;
    /**
     * mac地址
     */
    private String mac;
    /**
     * 归属
     */
    private String owner;

    private Integer typeId;
    private String deviceType;
    /**
     * 绑定状态
     */
    private String bindStatus;
    /**
     * 启用状态
     */
    private String enableStatus;
    /**
     * 集群id
     */
    private Integer groupId;
    /**
     * 集群名称
     */
    private String groupName;
    /**
     * 工作状态
     */
    private String WorkStatus;
    /**
     * 在线状态
     */
    private String onlineStatus;

    private Integer id;

    private Integer modelId;

    /**
     * 设备型号
     */
    private String modelName;

    private Long createTime;

    private Long lastUpdateTime;

    private String bindCustomer;

    private String location;


}