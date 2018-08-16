package com.huanke.iot.manage.vo.request.device.typeModel;

import lombok.Data;

/**
 * @author caikun
 * @date 2018年08月17日 00:01
 **/
@Data
public class DeviceModelQueryRequest {

    private String name;
    private Integer typeId; //类型id
    private Integer customerId;
    private Integer productId;
    private Integer status;

    private Integer page = 1;
    private Integer limit = 20;
}
