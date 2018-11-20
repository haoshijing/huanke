package com.huanke.iot.manage.vo.response.device.customer;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class CustomerUserListVo {
    private Integer id;
    private String nickname;
    private String province;
    private String city;
    private Integer typeId;
    private String typeName;
    private Integer groupId;
    private String groupName;
    private Integer modelId;
    private String modelName;
    private Long lastVisitTime;
}
