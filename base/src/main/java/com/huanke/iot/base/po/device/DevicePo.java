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
    private Integer deviceTypeId;
    private Integer productId;
    private Integer projectId;
    private Long insertTime;
    private Long lastUpdateTime;
}
