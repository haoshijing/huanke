package com.huanke.iot.base.po.device.team;

import com.huanke.iot.base.constant.CommonConstant;
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
     * 创建人
     */
    private Integer createUserId;

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
    private Integer manageUserIds;

    /**
     *组的状态1正常，2不正常
     */
    private Integer status = CommonConstant.STATUS_YES;

    //组状态，1-普通终端组，2-托管组
    private Integer teamStatus;
    //组类型，1-用户组，2-，3-自成组
    private Integer teamType;
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
    private String sceneDescription;

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
