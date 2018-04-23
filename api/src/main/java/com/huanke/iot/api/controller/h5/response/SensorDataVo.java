package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

import java.util.List;

@Data
public class SensorDataVo{
    private String name;
    private String type;
    private String unit;
    private List<String> xdata;
    private List<String> ydata;
}
