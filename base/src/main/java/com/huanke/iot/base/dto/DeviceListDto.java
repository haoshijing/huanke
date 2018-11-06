package com.huanke.iot.base.dto;

import lombok.Data;

/**
 * 描述:
 * 设备列表dto
 *
 * @author onlymark
 * @create 2018-11-06 上午10:48
 */
@Data
public class DeviceListDto {
    private Integer deviceId;
    private String mac;
    private String wxDeviceId;
    private Integer onlineStatus;
    private String deviceName;
    private String location;
    private String ip;
    private String modelName;
    private Integer typeId;
    private String typeName;
    private String typeNo;
    private String typeIcon;
    private String formatName;
}
