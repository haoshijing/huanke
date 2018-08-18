package com.huanke.iot.base.po.customer;

import lombok.Data;

@Data
public class WxConfigPo {

    private Integer id;
    private Integer customerId;
    private String password;
    private String defaultTeamName;
    private Integer htmlTypeId;
    private String backgroundImg;
    private String themeName;
    private String logo;
    private String version;
    private Long createTime;
    private Long lastUpdateTime;
}
