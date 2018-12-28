package com.huanke.iot.manage.vo.response.device.data;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-12-28 下午3:08
 */
@Data
public class SensorDataVo {
    private String name;
    private String type;
    private String unit;
    private List<String> xdata;
    private List<String> ydata;
}
