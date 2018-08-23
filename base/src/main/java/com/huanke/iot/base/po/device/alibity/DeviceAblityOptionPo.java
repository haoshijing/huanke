package com.huanke.iot.base.po.device.alibity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityOptionPo {

    private Integer id;
    private String optionName;
    private String optionValue;
    private Integer ablityId;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
