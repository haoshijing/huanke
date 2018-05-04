package com.huanke.iot.api.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpeedConfigRequest {
    private String deviceStr;
    private List<Integer> inSpeed;
    private List<Integer> outSpeed;
}
