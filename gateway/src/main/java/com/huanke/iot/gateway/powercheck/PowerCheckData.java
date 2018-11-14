package com.huanke.iot.gateway.powercheck;

import lombok.Data;

@Data
public class PowerCheckData {
    private Integer id;
    private Long lastUpdateTime;
    private Integer failCount;
    private boolean isPowerOn;
}
