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
    //绑定状态
    private Boolean bindStatus;
    //启用状态
    private Boolean enableStatus;
    //集群名
    private Boolean groupName;
    //工作状态
    private Boolean userName;
    //开关机状态
    private Boolean powerStatus;
    //在线状态
    private Boolean onlineStatus;
    //设备型号ID
    private Boolean assignStatus;
    //设备ID
    private Boolean id;
    //设备型号名称
    private Boolean modelName;
    //注册时间
    private Boolean birthTime;
    //最后上上线时间
    private Boolean lastOnlineTime;
    //创建人
    private Boolean createUserName;
    //地理位置
    private Boolean location;
    //管理名称
    private Boolean manageName;

    private String fileName;

    private String sheetTitle;

    private DeviceListQueryRequest deviceListQueryRequest;
}
