package com.huanke.iot.base.po.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * 安卓背景表
 */
@Data
public class WxBgImgPo {

    private Integer id;
    private Integer configId;   //安卓配置ID
    private Integer customerId; //客户主键
    private String name;    //图片名称
    private String bgImg;    //图片或视频
    private String description;    //描述介绍
    private Integer status = CommonConstant.STATUS_YES;    //状态
    private Long createTime;
    private Long lastUpdateTime;

}
