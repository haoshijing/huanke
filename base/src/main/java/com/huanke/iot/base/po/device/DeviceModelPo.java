package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月13日 19:23
 **/
@Data
public class DeviceModelPo {
    private Integer id;
    private String name;
    private Integer typeId;//类型id
    private String icon;//缩略图
    private Integer customerId;
    private Integer productId;
    private String version;
    private String status;
    private String remark;
    private Long createTime;
    private Long lastUpdateTime;
}
