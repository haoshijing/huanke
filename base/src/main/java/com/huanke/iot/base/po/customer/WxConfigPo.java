package com.huanke.iot.base.po.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class WxConfigPo {

    private Integer id;
    private Integer customerId;
    private String serviceUser;
    private String password;
    private String defaultTeamName;
    private String htmlTypeIds;
    private String backgroundImg;
    private String themeName;
    private String logo;
    private String version;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
