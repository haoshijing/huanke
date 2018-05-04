package com.huanke.iot.api.controller.h5.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DeviceSpeedConfigVo {

    private List<SpeedConfigItem> inItems;
    private List<SpeedConfigItem> outItems;
    @Data
    @AllArgsConstructor
    public static class SpeedConfigItem{
        private Integer level;
        private Integer speed;
    }
}
