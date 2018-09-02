package com.huanke.iot.manage.vo.request.device.team;

import lombok.Data;

@Data
public class TeamTrusteeRequest {
    private Integer id;
    private String openId;
    private Boolean deleteCreator;
}
