package com.huanke.iot.api.controller.machine.response;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月09日 12:58
 **/
@Data
public class MachineDeviceVo {
    private Integer deviceId;
    private String wechatDeviceId;
    private String devicelicence;
}
