package com.huanke.iot.base.po.device;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class DeviceIdPoolPo {
    private Integer id;
    private Integer customerId;
    private String deviceId;
    private String deviceLicence;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
