package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author onlymark
 * @version 2018年08月21日
 **/
@Data
public class DeviceCustomerUserRelationPo {
    private Integer id;
    private Integer customerId;
    private String openId;
    private String parentOpenId;
    private Integer deviceId;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
