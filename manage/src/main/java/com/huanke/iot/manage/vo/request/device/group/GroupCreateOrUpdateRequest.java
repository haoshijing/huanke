package com.huanke.iot.manage.vo.request.device.group;

import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import lombok.Data;

import java.util.List;

@Data
public class GroupCreateOrUpdateRequest {
    /**
     * 集群名称
     */
    private String name;

    /**
     * B端客户id
     */
    private Integer customerId;

    private String introduction;

    private String remark;

    private String createLocation;


    private DeviceQueryRequest deviceQueryRequest;

}
