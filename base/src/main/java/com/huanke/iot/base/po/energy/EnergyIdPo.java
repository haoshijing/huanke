package com.huanke.iot.base.po.energy;

import lombok.Data;


@Data
public class EnergyIdPo {
    private Integer id;
    private String name;
    private String energyId;
    private String pageId;
    private String pageName;
    private String pageMatchId;
    private Integer status;
    private String type;
    private Integer createUser;
    private Long createTime;
    private Integer lastUpdateUser;
    private Long lastUpdateTime;
    private Long lastOnlineTime;
}