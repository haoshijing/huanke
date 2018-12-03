package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

@Data
public class UserRepairInfo {
    private Integer deviceId;
    private Integer ruleId;
    private String description;
}
