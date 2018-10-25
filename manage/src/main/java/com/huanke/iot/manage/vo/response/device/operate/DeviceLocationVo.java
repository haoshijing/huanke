package com.huanke.iot.manage.vo.response.device.operate;

import lombok.Data;

@Data
public class DeviceLocationVo {
    private String province;
    private String city;
    private String area;
    private String location;
    private String pointX;
    private String pointY;
}
