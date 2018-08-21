package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceAssignToCustomerRequest {
    private Integer customerId;
    private String customerName;
    private String appid;
    private String deviceType;
    private String deviceTypeId;
    private String remark;
    private List<DeviceCreateOrUpdateRequest> deviceCreateOrUpdateRequests;
}
