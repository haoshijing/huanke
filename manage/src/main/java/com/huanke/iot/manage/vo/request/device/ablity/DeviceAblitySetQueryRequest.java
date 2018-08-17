package com.huanke.iot.manage.vo.request.device.ablity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblitySetQueryRequest {

    private Integer id;
    private String name;
    private Integer status;
    private String remark;
    private Integer page = 1;
    private Integer limit = 20;
}
