package com.huanke.iot.base.po.device.ability;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Caik
 * @date 2018/8/16 15:19
 */
@Data
public class DeviceTypeAbilitysPo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer typeId;
    private Integer abilityId;
    private Integer abilityType;
    private String abilityName;
    private String dirValue;
    private Integer minVal;
    private Integer maxVal;
    private Integer abilityStatus;

}
