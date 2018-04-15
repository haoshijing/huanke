package com.huanke.iot.api.controller.h5.group;

import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupNewRequest {
    private String groupName;
    private List<String> deviceIds;
}
