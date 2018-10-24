package com.huanke.iot.manage.vo.response.device.group;

import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupListVo {
    private Integer id;
    private String name;
    private String remark;
    private Integer customerId;
    private String customerName;
    private String introduction;
    private String location;
    private Integer deviceCount;
    private Integer status;
    private Long createTime;
    private List<DeviceInGroup> deviceList;
    @Data
    public static class DeviceInGroup{
        private Integer id;
        private String name;
        private String mac;
        private String location;
        private Integer modelId;
        private String productName;
        private Integer onlineStatus;
        private Integer workStatus;
        //todo 告警状态

        private String icon;
    }
}
