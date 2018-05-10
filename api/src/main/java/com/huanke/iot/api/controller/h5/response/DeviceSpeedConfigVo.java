package com.huanke.iot.api.controller.h5.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class DeviceSpeedConfigVo {

    private List<SpeedConfigItem> inItems;
    private List<SpeedConfigItem> outItems;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpeedConfigItem{
        private Integer level;
        private Integer speed;
    }
}
