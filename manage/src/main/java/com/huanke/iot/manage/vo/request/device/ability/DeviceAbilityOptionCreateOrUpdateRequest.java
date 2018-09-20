package com.huanke.iot.manage.vo.request.device.ability;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAbilityOptionCreateOrUpdateRequest {

    private Integer id;
    private String optionName;
    private String optionValue;
    private Integer minVal;
    private Integer maxVal;
    private Integer status = CommonConstant.STATUS_YES;
//    private String abilityId;
}
