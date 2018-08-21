package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceCreateOrUpdateRequest {
    /**
     * 数据库中的id
     */
    private Integer id;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备类型id
     */
    private Integer typeId;
    /**
     * 设备mac地址
     */
    private String mac;
    /**
     * 生产日期
     */
    private Long createTime;

    List<DeviceCreateOrUpdateRequest> deviceCreateOrUpdateRequests;

}
