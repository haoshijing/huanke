package com.huanke.iot.api.controller.h5.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述:
 * 设备群Vo
 *
 * @author onlymark
 * @create 2018-09-24 上午11:01
 */
@Data
public class DeviceGroupVo {
    private Integer id;
    private String name;
    private List<DeviceGroupItem> deviceGroupItemList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceGroupItem{
        private Integer deviceId;
        private String wxDeviceId;
        private String deviceName;
    }
}
