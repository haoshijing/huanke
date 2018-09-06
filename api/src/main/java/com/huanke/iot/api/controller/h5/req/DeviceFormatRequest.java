package com.huanke.iot.api.controller.h5.req;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class DeviceFormatRequest {
    @NotNull
    private String wxDeviceId;
    @NotNull
    private Integer pageId;
}
