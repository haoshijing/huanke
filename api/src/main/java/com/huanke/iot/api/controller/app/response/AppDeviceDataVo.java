package com.huanke.iot.api.controller.app.response;

import com.huanke.iot.api.controller.h5.response.DeviceAbilitysVo;
import lombok.Data;

import java.util.List;

@Data
public class AppDeviceDataVo {
    private Integer id;
    private String dirValue;
    private String abilityName;
    private Integer abilityType;
    private String currValue;
    private String unit;
    private List<DeviceAbilitysVo.abilityOption> abilityOptionList;

    @Data
    public static class abilityOption{
        private String dirValue;
        private Integer isSelect;
        private String currValue;
    }
}
