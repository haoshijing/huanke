package com.huanke.iot.manage.vo.response.device.ability;

import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceTypeAbilitysVo {

    private Integer id;
    private Integer typeId;
    private Integer abilityId;
    private String abilityName;
    private Integer abilityType;
    private Integer minVal;
    private Integer maxVal;

    private List<DeviceAbilityOptionVo> deviceAbilityOptions;
}
