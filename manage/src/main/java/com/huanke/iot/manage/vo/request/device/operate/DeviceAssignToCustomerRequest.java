package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceAssignToCustomerRequest {
    private Integer customerId;
    private String appid;

    private Integer modelId;
    private String productId;

    private DeviceQueryRequest deviceQueryRequest;
}