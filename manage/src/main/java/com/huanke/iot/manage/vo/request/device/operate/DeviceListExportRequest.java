package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

@Data
public class DeviceListExportRequest {
    //名称
    private Boolean displayName;
    //MAC
    private Boolean displayMac;
    //归属
    private Boolean displayOwner;
    //类型
    private Boolean displayType;
    //绑定状态
    private Boolean displayBindStatus;
    //启用状态
    private Boolean displayEnableStatus;
    //集群名
    private Boolean displayGroupName;
    //工作状态
    private Boolean displayWorkStatus;
    //在线状态
    private Boolean displayOnlineStatus;
    //设备型号ID
    private Boolean displayModelId;
    //设备型号名称
    private Boolean displayModelName;
    //注册时间
    private Boolean displayBirthName;
    //最后上上线时间
    private Boolean displayLastOnlineTime;
    //地理位置
    private Boolean displayLocation;
}
