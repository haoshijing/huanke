package com.huanke.iot.base.po.device.ability;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAbilityPo implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String abilityName;
    private String dirValue;
    private Integer writeStatus; //可写状态
    private Integer readStatus; //可读状态
    private Integer runStatus; //可执行状态
    private String paramName; //给设备传参数名称
    private Integer configType;//配置方式
    private Integer abilityType;//能力类型
    private Integer minVal;
    private Integer maxVal;
    private String remark;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer createUserId;
    private Integer updateUserId;
}
