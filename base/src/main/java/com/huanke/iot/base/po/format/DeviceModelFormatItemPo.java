package com.huanke.iot.base.po.format;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/27 13:13
 */
@Data
public class DeviceModelFormatItemPo {
    private Integer id;
    private Integer modelFormatId;
    private Integer itemId;
    private Integer ablityId;
    private Integer showStatus;
    private String showName;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
