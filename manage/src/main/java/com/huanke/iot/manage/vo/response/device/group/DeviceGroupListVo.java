package com.huanke.iot.manage.vo.response.device.group;

import lombok.Data;

@Data
public class DeviceGroupListVo {
    private Integer id;
    private String name;
    private String remark;
    private Integer customerId;
    private String customerName;
    private String introduction;
    private String location;
    private Integer deviceCount;
    private Integer status;
    private Long createTime;
}
