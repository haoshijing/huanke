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

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 最后修改时间
     */
    private Long lastUpdateTime;
}
