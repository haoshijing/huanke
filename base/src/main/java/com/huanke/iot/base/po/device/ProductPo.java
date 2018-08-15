package com.huanke.iot.base.po.device;

import lombok.Data;

@Data
public class ProductPo {
    private Integer id;
    /**
     * 产品编号
     */
    private String key;
    private String name;
    private Long insertTime;
    private Long lastUpdateTime;
}
