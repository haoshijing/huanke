package com.huanke.iot.base.po.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Caik
 * @date 2018/8/16 15:19
 */
@Data
public class DeviceModelPo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String modelNo;
    private String modelCode;
    private Integer typeId; //类型id
    private String typeName; //类型id
    private Integer customerId;
    private String customerName;
    private String productQrCode;
    private String productId;
    private Integer formatId;
    private Integer androidFormatId;
    private String icon;
    private String version;
    private Integer status = CommonConstant.STATUS_YES; //默认正常状态
    private String remark;
    private String childModelIds;
    private Long createTime;
    private Integer createUser;
    private Long lastUpdateTime;
    private Integer lastUpdateUser;

}
