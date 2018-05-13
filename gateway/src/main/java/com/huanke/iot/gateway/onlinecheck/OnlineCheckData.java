package com.huanke.iot.gateway.onlinecheck;

import lombok.Data;

@Data
public class OnlineCheckData {

    private Integer id;
    private Long lastUpdateTime;
    private Integer failCount;
    private boolean isOnline;
}
