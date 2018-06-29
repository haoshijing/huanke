package com.huanke.iot.base.po.publicnumber;

import lombok.Data;

@Data
public class PublicNumberPo {
    private Integer id;
    private String appId;
    private String appSecret;
    private String name;
    private Long insertTime;
    private Long lastUpdateTime;
}
