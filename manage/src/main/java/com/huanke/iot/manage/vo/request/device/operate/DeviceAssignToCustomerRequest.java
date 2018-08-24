package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceAssignToCustomerRequest {
    private Integer customerId;
    private String appid;

    private Integer modelId;
    private Integer productId;

    private DeviceQueryRequest deviceQueryRequest;
}