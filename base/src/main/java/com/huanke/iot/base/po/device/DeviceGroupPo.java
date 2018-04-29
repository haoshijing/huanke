package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:20
 **/
@Data
public class DeviceGroupPo {
    /**
     * 编组id
     */
    private Integer id;
    /**
     * 编组名称
     */
    private String groupName;
    private Integer userId;

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 最后修改时间
     */
    private Long lastUpdateTime;

    /*
    组的状态1-正常2-已删除
     */
    private Integer status;

    /**
     * 组图标
     */
    private String icon;
    /**
     * 文字介绍
     */
    private String memo;
    /**
     * 视频封面图
     */
    private String videoCover;
    /**
     * 视频链接
     */
    private String videoUrl;
}
