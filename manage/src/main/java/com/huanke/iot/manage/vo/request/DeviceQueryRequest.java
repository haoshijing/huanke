package com.huanke.iot.manage.vo.request;

import lombok.Data;

/**
 * @author sixiaojun
 * @version 2018-08-15
 **/
@Data
public class DeviceQueryRequest {
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
    /**
     * 设备类型
     */
    private Integer DeviceTypeId;
    /**
     * 绑定状态
     */
    private Integer bindStatus;
    /**
     * 启用状态
     */
    private String usingStatus;
    /**
     * 集群id
     */
    private String group_id;
    /**
     * 集群名称
     */
    private String group_name;
    /**
     * 工作状态
     */
    private String workingStatus;
    /**
     * 在线状态
     */
    private Integer onlineStatus;
    /**
     * 分页
     */
    private Integer page = 1;
    /**
     * 每页显示数量
     */
    private Integer limit = 20;
}
