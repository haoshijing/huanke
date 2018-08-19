package com.huanke.iot.base.po.device.typeModel;

import lombok.Data;

/**
 * @author caikun
 * @date 2018/8/19 上午9:38
 **/

@Data
public class DeviceModelAblityPo {
    private Integer id;
    private Integer modelId;
    private Integer ablityId;
    private String definedName;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
