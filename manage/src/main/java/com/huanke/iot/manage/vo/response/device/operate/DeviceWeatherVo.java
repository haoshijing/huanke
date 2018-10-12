package com.huanke.iot.manage.vo.response.device.operate;

import lombok.Data;

@Data
public class DeviceWeatherVo {
    private String outerHum;
    private String outerPm;
    private String outerTem;
    private String weather;
}
