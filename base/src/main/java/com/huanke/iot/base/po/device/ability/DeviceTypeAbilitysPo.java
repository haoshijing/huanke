package com.huanke.iot.base.po.device.ability;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/16 15:19
 */
@Data
public class DeviceTypeAbilitysPo {

    private Integer id;
    private Integer typeId;
    private Integer abilityId;
    private Integer abilityType;
    private String abilityName;
    private Integer minVal;
    private Integer maxVal;

}
