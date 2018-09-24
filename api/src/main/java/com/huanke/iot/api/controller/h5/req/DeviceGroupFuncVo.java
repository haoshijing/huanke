package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupFuncVo {
    private List<String> wxDeviceIdList;
    private String funcId;
    private String value;
}
