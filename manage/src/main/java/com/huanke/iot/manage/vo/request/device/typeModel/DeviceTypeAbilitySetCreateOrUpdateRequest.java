package com.huanke.iot.manage.vo.request.device.typeModel;


import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月16日 23:51
 **/
@Data
public class DeviceTypeAbilitySetCreateOrUpdateRequest {
    private Integer id;
    private Integer typeId;
    private Integer abilitySetId;
}
