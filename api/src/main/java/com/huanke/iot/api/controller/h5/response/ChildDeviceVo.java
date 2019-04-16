package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 下午1:15
 */
@Data
public class ChildDeviceVo {
    private Integer id;
    private String customerName;
    private String deviceName;
    private String childId;
    private String deviceTypeName;
    private String formatName;
    private Integer onlineStatus;//从机在线状态
    private Integer hostPowerStatus;//主机开关状态
}
