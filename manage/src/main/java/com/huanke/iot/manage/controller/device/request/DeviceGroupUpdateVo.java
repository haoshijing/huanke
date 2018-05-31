package com.huanke.iot.manage.controller.device.request;

import lombok.Data;

@Data
public class DeviceGroupUpdateVo {
    private Integer id;
    private String groupName;
    private String icon;
    private String coverUrl;
    private String videoUrl;
    private String memo;
}
