package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * @author onlymark
 * @version 2018年08月20日
 **/
@Data
public class DeviceTeamPo {
    /**
     * id
     */
    private Integer id;

    /**
     * 图标，缩略图
     */
    private String icon;

    /**
     * 组名
     */
    private String name;

    /**
     * 备注，描述
     */
    private String remark;

    /**
     * 组控制人
     */
    private Integer masterUserId;

    /**
     * B端客户id
     */
    private Integer customerId;

    /**
     * 组管理员
     */
    private String manageUserIds;

    /**
     *组的状态1-正常2-已删除
     */
    private Integer status = 1;

    /**
     * 二维码链接
     */
    private String qrcode;

    /**
     * 广告图片
     */
    private String adImages;

    /**
     * '分组说明'
     */
    private String memo;

    /**
     * '分组封面'
     */
    private String videoCover;

    /**
     * '分组视频链接'
     */
    private String videoUrl;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后修改时间
     */
    private Long lastUpdateTime;

}
