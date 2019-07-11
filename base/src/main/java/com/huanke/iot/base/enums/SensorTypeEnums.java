package com.huanke.iot.base.enums;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public enum SensorTypeEnums {

    PM25_IN("110", "PM2.5", "ug/m3"),
    PM25_OUTER("111", "室外PM2.5", "ug/m3"),
    PM25_WEATHER("11F","天气PM2.5","ug/m3"),
    CO2_IN("120", "二氧化碳", "PPM"),
    CO2_OUTER("121", "室外二氧化碳", "PPM"),
    HUMIDITY_IN("130", "湿度", "%"),
    HUMIDITY_OUTER("131", "室外湿度", "%"),
    HUMIDITY_WEATHER("13F","天气湿度","%"),
    TEMPERATURE_IN("140", "温度", "℃"),
    TEMPERATURE_OUTER("141", "室外温度", "℃"),
    TEMPERATURE_WEATHER("14F","天气温度","℃"),
    TVOC_IN("150", "TVOC", "mg/m³"),
    TVOC_OUTER("151", "室外TVOC", "mg/m³"),
    HCHO_IN("160", "甲醛", "mg/m³"),
    HCHO_OUTER("161", "室外甲醛", "mg/m³"),
    NH3_IN("170","氨气","ppm"),
    OUT_WATER_TEM("142","出水温度","℃"),
    IN_WATER_TEM("143","回水温度","℃"),

    /**
     * 1-晴,2-多云,3-阴,4-阵雨,5-雷阵雨,6-雷阵雨有冰雹,7-雨夹雪,8-小雨,9-中雨,
     * 10-大雨,11-暴雨,12-大暴雨,13-特大暴雨,14-阵雪,15-小雪,16-中雪,17-大雪,
     * 18-暴雪,19-雾,20-冻雨,21-沙尘暴,22-小雨-中雨,23-中雨-大雨,24-大雨-暴雨,
     * 25-暴雨-大暴雨,26-大暴雨-特大暴雨,27-小雪-中雪,28-中雪-大雪,29-大雪-暴雪,
     * 30-浮尘,31-扬沙,32-强沙尘暴,33-霾
     */
	WEATHER("17F","天气",""),
    ANION_IN("180","负离子","个/cm³");

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
