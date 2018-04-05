package com.huanke.iot.base.po;

import lombok.Data;

@Data
public class ProjectPo {
    private Integer id;

    /**
     * 项目编号
     */
    private String key;
    private String name;
    private Long insertTime;
    private Long lastUpdateTime;
}
