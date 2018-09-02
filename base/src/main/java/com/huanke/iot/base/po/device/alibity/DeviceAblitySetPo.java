package com.huanke.iot.base.po.device.alibity;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblitySetPo {

    private Integer id;
    private String name;
    private Integer status = CommonConstant.STATUS_YES;
    private String remark;
    private Long createTime;
    private Long lastUpdateTime;
}
