package com.huanke.iot.base.po.device.ability;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAbilityOptionPo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String optionName;
    private String optionValue;
    private Integer abilityId;
    private Integer status = CommonConstant.STATUS_YES;
    private Integer defaultValue;
    private Integer minVal;
    private Integer maxVal;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer createUserId;
    private Integer updateUserId;
}
