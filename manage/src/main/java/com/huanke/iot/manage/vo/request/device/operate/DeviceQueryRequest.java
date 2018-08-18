package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

/**
 * @author sixiaojun
 * @version 2018-08-15
 * 查询设备列表返回类
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
    private String bindStatus;
    /**
     * 启用状态
     */
    private String enableStatus;
    /**
     * 集群id
     */
    private Integer group_id;
    /**
     * 集群名称
     */
    private String group_name;
    /**
     * 工作状态
     */
    private String WorkStatus;
    /**
     * 在线状态
     */
    private String onlineStatus;
    /**
     * 分页
     */
    private Integer page = 1;
    /**
     * 每页显示数量
     */
    private Integer limit = 20;
}
