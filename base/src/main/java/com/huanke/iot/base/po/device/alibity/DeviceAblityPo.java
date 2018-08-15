package com.huanke.iot.base.po.device.alibity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityPo {

    private Integer id;
    private String ablityName;
    private String dirValue;
    private String remark;
    private Integer writeStatus; //可读写状态
    private Long createTime;
    private Long lastUpdateTime;
}
