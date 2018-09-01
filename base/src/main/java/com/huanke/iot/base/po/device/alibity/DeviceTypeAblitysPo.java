package com.huanke.iot.base.po.device.alibity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/16 15:19
 */
@Data
public class DeviceTypeAblitysPo {

    private Integer id;
    private Integer typeId;
    private Integer ablityId;
    private Integer ablityType;
    private String ablityName;
    private Long createTime;
    private Long lastUpdateTime;

}
