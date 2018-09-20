package com.huanke.iot.manage.vo.request.device.typeModel;

import lombok.Data;

/**
 * @author caikun
 * @date 2018年08月17日 00:01
 **/
@Data
public class DeviceTypeAbilityQueryRequest {
    private Integer id;
    private Integer typeId;
    private Integer abilitySetId;
    private Integer page = 1;
    private Integer limit = 20;
}
