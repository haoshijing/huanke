package com.huanke.iot.manage.response;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:13
 **/
@Data
public class DeviceVo {
    private String deviceId;
    private String name;
    private String mac;
    private String bindStatus;
    private Integer id;
    private String software;
    private String hardware;
    private String onlineStatus;
}
