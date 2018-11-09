package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 下午2:05
 */
@Data
public class LocationVo {

    private String province;
    private String city;
    private String area;
    private String location;
    private String mapGps;
}
