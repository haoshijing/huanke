package com.huanke.iot.manage.vo.response.device.ability;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAbilityOptionVo {

    private Integer id;
    private String optionName;
    private String optionValue;
    private Integer defaultVal;
    private Integer minVal;
    private Integer maxVal;
    private Integer status = CommonConstant.STATUS_YES;
}
