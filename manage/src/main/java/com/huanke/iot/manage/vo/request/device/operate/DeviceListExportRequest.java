package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

@Data
public class DeviceListExportRequest {
    //名称
    private Boolean name;
    //MAC
    private Boolean mac;
    //归属
    private Boolean customerName;
    //类型
    private Boolean deviceType;
    //绑定状态
    private Boolean bindStatus;
    //启用状态
    private Boolean enableStatus;
    //集群名
    private Boolean groupName;
    //工作状态
    private Boolean workStatus;
    //在线状态
    private Boolean onlineStatus;
    //设备型号ID
    private Boolean modelId;
    //设备型号名称
    private Boolean modelName;
    //注册时间
    private Boolean birthTime;
    //最后上上线时间
    private Boolean lastUpdateTime;
    //地理位置
    private Boolean location;

    private String fileName;

    private String sheetTitle;

    private DeviceListQueryRequest deviceListQueryRequest;
}
