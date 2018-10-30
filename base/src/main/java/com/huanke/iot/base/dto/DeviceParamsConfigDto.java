package com.huanke.iot.base.dto;

import lombok.Data;

/**
 * 描述:
 * 设备传参详情dto
 *
 * @author onlymark
 * @create 2018-10-29 下午4:56
 */
@Data
public class DeviceParamsConfigDto {
    private Integer abilityId;
    private String modelAbilityName;
    private String abilityParamsName;
    private Integer sort;
    private String value;
}
