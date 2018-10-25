package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceCreateOrUpdateRequest {

    private List<DeviceUpdateList> deviceList;

    @Data
    public static class DeviceUpdateList {
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
        /**
         * 生产日期
         */
        private Long birthTime;

        private  String hardVersion;
        /**
         * 创建用户id
         */
        private Integer createUserId;
        /**
         * 修改用户id
         */
        private Integer updateUserId;

    }
}