package com.huanke.iot.manage.vo.request.device.ablity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityQueryRequest {

    private Integer id;
    private String ablityName;
    private String dirValue;
    private Integer writeStatus; //可写状态
    private Integer readStatus; //可读状态
    private Integer runStatus; //可执行状态
    private Integer configType;//配置方式
    private Integer ablityType;//能力类型

    private Integer page = 1;
    private Integer limit = 20;
}
