package com.huanke.iot.manage.vo.response.device.group;

import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupDetailVo {
    private Integer id;
    private String name;
    private String remark;
    private Integer customerId;
    private String customerName;
    private String introduction;
    private Integer deviceCount;
    private Long createTime;
    private List<DeviceInGroup> deviceList;
    private List<Images> imagesList;
    private List<Videos> videosList;
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
    @Data
    public static class Images{
        private String image;
    }
    @Data
    public static class Videos{
        private String video;
    }
}
