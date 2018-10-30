package com.huanke.iot.base.dto;

import lombok.Data;

/**
 * 描述:
 * 设备传参dto
 *
 * @author onlymark
 * @create 2018-10-29 下午2:40
 */
@Data
public class DeviceParamsDto {
    private Integer abilityId;
    private String modelAbilityName;
    private String abilityParamsName;
}
