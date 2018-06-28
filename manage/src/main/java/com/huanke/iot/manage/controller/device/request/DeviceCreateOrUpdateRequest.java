package com.huanke.iot.manage.controller.device.request;

import lombok.Data;

@Data
public class DeviceCreateOrUpdateRequest {
    private Integer id;

    /**
     * 设备mac地址
     */
    private String mac;

    /*
    设备名称
     */
    private String name;
    /**
     * 设备类型id
     */
    private Integer deviceTypeId;
    /**
     * 公众号id
     */
    private Integer publicId;

    private String productId;
}
