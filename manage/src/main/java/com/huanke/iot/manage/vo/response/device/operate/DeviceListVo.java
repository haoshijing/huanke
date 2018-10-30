package com.huanke.iot.manage.vo.response.device.operate;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class DeviceListVo {

    /* 归属 */
    private Integer customerId;
    private String customerName;
    private String deviceType;
    private String modelName;

    /**
     *设备名
     */
    private String name;
    /**
     * mac地址
     */
    private String mac;
    private String deviceNo;

    private String wxDeviceId;

    private Integer typeId;
    /**
     * 分配状态
     */
    private Integer assignStatus;

    /**
     * 绑定状态
     */
    private Integer bindStatus;
    /**
     * 启用状态
     */
    private Integer enableStatus;
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
    private Integer workStatus;

    /**
     * 开关机状态
     */
    private Integer powerStatus;
    /**
     * 在线状态
     */
    private Integer onlineStatus;

    private Integer id;

    private Integer modelId;

    private Integer hostStatus;

    private Integer hostDeviceId;

    private String childId;

    private Integer childCount;

    /**
     * 设备型号
     */

    private Long birthTime;

    private Long lastUpdateTime;

    private String userOpenId;

    private String userName;

    private String location;

    private Integer status;

}