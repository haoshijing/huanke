package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceQueryRequest {

    private List<DeviceQueryList> deviceList;

    @Data
    public static class DeviceQueryList {
        /**
         * 设备名称
         */
        private String name;
        /**
         * 设备类型id
         */
        private Integer typeId;
        /**
         * 设备mac地址
         */
        private String mac;
    }
}