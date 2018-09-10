package com.huanke.iot.base.po.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/16 15:19
 */
@Data
public class DeviceModelPo {

    private Integer id;
    private String name;
    private String modelNo;
    private Integer typeId; //类型id
    private String typeName; //类型id
    private Integer customerId;
    private String customerName;
    private String productId;
    private Integer formatId;
    private String icon;
    private String version;
    private Integer status = CommonConstant.STATUS_YES; //默认正常状态
    private String remark;
    private Long createTime;
    private Long lastUpdateTime;

}
