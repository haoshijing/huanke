package com.huanke.iot.manage.vo.request.device.ablity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityOptionQueryRequest {

    private Integer id;
    private String optionName;
    private String ablityId;
    private Integer page = 1;
    private Integer limit = 20;
}
