package com.huanke.iot.base.enums;

import lombok.Getter;
import lombok.Setter;

public enum  SensorTypeEnums {

    PM25_IN(110,"室内pm值"),
    PM25_OUTER(111,"室外pm值"),
    CO2_IN(120,"室内二氧化碳"),
    CO2_OUTER(121,"室外二氧化碳"),
    HUMIDITY_IN(130,"室内湿度"),
    HUMIDITY_OUTER(131,"室外湿度"),
    TEMPERATURE_IN(140,"室内温度"),
    TEMPERATURE_OUTER(141,"室外温度"),
    TVOC_IN(150, "室内甲醛值"),
    TVOC_OUTER(151,"室外甲醛值"),
    HCHO_IN(160,"室内甲醛化学因子数"),
    HCHO_OUTER(161,"室外甲醛化学因子数");


    private SensorTypeEnums(int code,String mark){
        this.code = code;
        this.mark = mark;
    }
    @Getter @Setter
    private int code;
    @Getter @Setter
    private String mark;
}
