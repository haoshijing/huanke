package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceQueryRequest {

    private List<DeviceList> deviceList;

    @Data
    public static class DeviceList {
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
