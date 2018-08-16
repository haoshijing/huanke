package com.huanke.iot.base.po.device.typeModel;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/16 15:19
 */
@Data
public class DeviceModelPo {

    private Integer id;
    private String name;
    private Integer typeId; //类型id
    private Integer customerId;
    private Integer productId;
    private String icon;
    private String version;
    private Integer status;
    private String remark;
    private Long createTime;
    private Long lastUpdateTime;

}
