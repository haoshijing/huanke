package com.huanke.iot.manage.vo.request.type;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年06月01日 12:58
 **/
@Data
public class DeviceTypeQueryRequest {
    private String name;
    private Integer page = 1;
    private Integer limit = 20;
}
