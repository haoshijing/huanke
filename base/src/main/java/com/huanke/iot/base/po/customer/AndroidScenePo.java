package com.huanke.iot.base.po.customer;

import lombok.Data;

/**
 * 安卓场景
 */
@Data
public class AndroidScenePo {

    private Integer id;
    private Integer configId;   //配置表的Id
    private Integer customerId; //客户ID
    private String name;    //场景名称，如：白天，晚上等。
    private String imgsCover;   //图册封面
    private String describe;    //描述
    private Long createTime;
    private Long lastUpdateTime;    //最后更新时间
}
