package com.huanke.iot.api.controller.h5.team;

import lombok.Data;

import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月22日
 **/
@Data
public class DeviceTeamNewRequest {
    private String teamName;
    private List<String> wxDeviceIds;
}
