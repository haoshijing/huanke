package com.huanke.iot.base.po.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caikun
 * @date 2018/8/19 上午9:38
 **/

@Data
public class DeviceModelAbilityPo implements Serializable {
    private Integer id;
    private Integer modelId;
    private Integer abilityId;
    private Integer abilityType;
    private String dirValue;
    private String definedName;
    private Integer minVal;
    private Integer maxVal;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
