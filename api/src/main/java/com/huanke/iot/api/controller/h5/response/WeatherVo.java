package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 下午2:04
 */
@Data
public class WeatherVo {
    private String outerHum;
    private String outerPm;
    private String outerTem;
    private String weather;
}
