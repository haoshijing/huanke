package com.huanke.iot.manage.vo.request.customer;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class CustomerUserQueryRequest {
    private Integer customerId;
    private Integer typeId;
    private Integer modelId;
    private Integer groupId;
    private String province;
    private String city;
    private Integer status;

    private Integer page = 1;
    private Integer limit = 20;
}
