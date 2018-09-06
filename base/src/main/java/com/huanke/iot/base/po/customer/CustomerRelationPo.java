package com.huanke.iot.base.po.customer;

import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
@Data
public class CustomerRelationPo {
    private Integer id;
    private Integer customerId;
    private Integer deviceId;
    private Long createTime;
    private Long lastUpdateTime;
}
