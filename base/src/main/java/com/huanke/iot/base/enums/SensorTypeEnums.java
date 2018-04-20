package com.huanke.iot.base.enums;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public enum SensorTypeEnums {

    PM25_IN("110", "室内pm值", "ug/m3"),
    PM25_OUTER("111", "室外pm值", "ug/m3"),
    CO2_IN("120", "室内二氧化碳", "PPM"),
    CO2_OUTER("121", "室外二氧化碳", "PPM"),
    HUMIDITY_IN("130", "室内湿度", "%"),
    HUMIDITY_OUTER("131", "室外湿度", "%"),
    TEMPERATURE_IN("140", "室内温度", "℃"),
    TEMPERATURE_OUTER("141", "室外温度", "℃"),
    TVOC_IN("150", "室内甲醛值", "mg/m³"),
    TVOC_OUTER("151", "室外甲醛值", "mg/m³"),
    HCHO_IN("160", "室内甲醛化学因子数", "mg/m³"),
    HCHO_OUTER("161", "室外甲醛化学因子数", "mg/m³");

    SensorTypeEnums(String code, String mark, String unit) {
        this.code = code;
        this.mark = mark;
        this.unit = unit;
    }

    @Getter
    @Setter
    private String code;
    @Getter
    @Setter
    private String mark;

    @Getter
    @Setter
    private String unit;

    public static SensorTypeEnums getByCode(String code) {
        for (SensorTypeEnums sensorTypeEnums : SensorTypeEnums.values()) {
            if (StringUtils.equals(sensorTypeEnums.getCode(), code)) {
                return sensorTypeEnums;
            }
        }
        return null;
    }
}
