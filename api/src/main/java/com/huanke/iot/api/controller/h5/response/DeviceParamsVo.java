package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 设备参数Vo
 *
 * @author onlymark
 * @create 2018-10-30 上午11:12
 */
@Data
public class DeviceParamsVo {
    private String abilityTypeName;
    private Integer sort;
    private List<String> valuesList;
}
