package com.huanke.iot.api.controller.h5.req;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 描述:设备请求类
 *
 * @author onlymark
 * @create 2018-09-06 下午4:36
 */
@Data
@NoArgsConstructor
public class DeviceRequest {
    @NotNull
    private String wxDeviceId;
    private String deviceName;
}
