package com.huanke.iot.base.po.format;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/27 13:13
 */
@Data
public class DeviceModelFormatPo {
    private Integer id;
    private Integer modelId;
    private Integer formatId;
    private Integer pageId;
    private Integer showStatus;
    private String showName;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
