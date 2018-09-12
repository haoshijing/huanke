package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-12 下午8:06
 */
@Data
public class UpdateDeviceTeamRequest {
    private Integer teamId;
    private String teamName;
}
