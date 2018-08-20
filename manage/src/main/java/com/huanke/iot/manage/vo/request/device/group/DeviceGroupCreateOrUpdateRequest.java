package com.huanke.iot.manage.vo.request.device.group;

import com.huanke.iot.base.po.device.DevicePo;
import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupCreateOrUpdateRequest {
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
    private String masterOpenId;

    /**
     * 管理OpenId
     */
    private String manageOpenIds;

    private List<DeviceGroupAddNewDeviceRequest> deviceGroupAddNewDeviceRequests;

}
