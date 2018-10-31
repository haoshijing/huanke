package com.huanke.iot.base.po.device;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class DeviceIdPoolPo {
    private Integer id;
    private Integer customerId;
    private String productId;
    private String wxDeviceId;
    private String wxDeviceLicence;
    private String wxQrticket;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer createUser;
    private Integer lastUpdateUser;

}
