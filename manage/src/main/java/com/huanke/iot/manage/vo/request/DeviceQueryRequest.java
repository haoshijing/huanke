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
     * 分页
     */
    private Integer page = 1;
    /**
     * 每页显示数量
     */
    private Integer limit = 20;
}
