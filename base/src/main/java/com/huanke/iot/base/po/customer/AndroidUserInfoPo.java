package com.huanke.iot.base.po.customer;

import lombok.Data;

@Data
public class AndroidUserInfoPo {
    private Integer id;
    private Integer customerId;
    private String imei;
    private Integer custUserId;
    private Long createTime;
}
