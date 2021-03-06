package com.huanke.iot.manage.vo.response.device.ability;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/16 19:19
 */
@Data
public class DeviceAbilitySetVo {

    private Integer id;
    private String name;
    private Integer status = CommonConstant.STATUS_YES;
    private String remark;
}
