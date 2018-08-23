package com.huanke.iot.base.po.customer;

import lombok.Data;

/**
 * 安卓场景图册表
 */
@Data
public class AndroidSceneImgPo {

    private Integer id;
    private Integer configId;   //安卓配置ID
    private Integer androidSceneId; //安卓场景ID
    private Integer customerId; //客户主键
    private String name;    //图片名称
    private String imgVideo;    //图片或视频
    private String description;    //描述介绍
    private Long createTime;
    private Long lastUpdateTime;

}
