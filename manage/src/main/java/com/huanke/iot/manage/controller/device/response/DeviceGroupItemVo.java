package com.huanke.iot.manage.controller.device.response;

import lombok.Data;

@Data
public class DeviceGroupItemVo {
    private String groupName;
    private String icon;
    private Integer id;
    /**
     * 管理员名字
     */
    private String maskNickname;
    private String videoUrl;
    private String videoCover;
    private String memo;

}
