package com.huanke.iot.manage.vo.request.device.ability;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAbilityOptionQueryRequest {

    private Integer id;
    private String optionName;
    private Integer abilityId;
    private Integer page = 1;
    private Integer limit = 20;
}
