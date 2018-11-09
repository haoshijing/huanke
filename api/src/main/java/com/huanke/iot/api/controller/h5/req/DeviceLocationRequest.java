package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-17 下午6:12
 */
@Data
public class DeviceLocationRequest {
    private Integer deviceId;
    private String location;
    private String[] gps;
}
