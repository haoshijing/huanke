package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class DeviceCustomerRelationPo {
    private Integer id;
    private Integer customerId;
    private String customerName;
    private Integer deviceId;
    private Long createTime;
    private Long lastUpdateTime;
}
