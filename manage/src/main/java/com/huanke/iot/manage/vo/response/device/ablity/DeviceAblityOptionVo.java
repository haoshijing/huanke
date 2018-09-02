package com.huanke.iot.manage.vo.response.device.ablity;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityOptionVo {

    private Integer id;
    private String optionName;
    private String optionValue;
    private Integer minVal;
    private Integer maxVal;
    private Integer status = CommonConstant.STATUS_YES;
}
