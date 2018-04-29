package com.huanke.iot.manage.controller.request;

import lombok.Data;

@Data
public class OtaDeviceRequest {

    private Integer id;
    private String fileName;
    private String type;
}
