package com.huanke.iot.api.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpeedConfigRequest {
    private String deviceId;
    private List<Integer> inSpeed;
    private List<Integer> outSpeed;
}
