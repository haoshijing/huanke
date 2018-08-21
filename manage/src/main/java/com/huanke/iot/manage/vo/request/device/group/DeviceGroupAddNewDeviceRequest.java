package com.huanke.iot.manage.vo.request.device.group;


import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupAddNewDeviceRequest {

    /**
     * 集群ID
     */
    private Integer groupId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备类型id
     */
    private Integer typeId;
    /**
     * 设备mac地址
     */
    private String mac;

    private List<DeviceGroupAddNewDeviceRequest> deviceGroupAddNewDeviceRequests;


}
