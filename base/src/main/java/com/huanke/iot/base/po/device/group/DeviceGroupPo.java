package com.huanke.iot.base.po.device.group;

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
    private String name;

    /**
     * B端客户id
     */
    private Integer customerId;

    /**
     * 父openId
     */
    private Integer masterUserId;

    /**
     * 管理OpenId
     */
    private String manageUserIds;

    /**
     *组的状态1-正常2-已删除
     */
    private Integer status = 1;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后修改时间
     */
    private Long lastUpdateTime;

}
