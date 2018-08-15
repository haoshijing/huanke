package com.huanke.iot.base.po.customer;

import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
@Data
public class CustomerPo {
    private Integer id;
    private String name;
    private String loginname;
    private Integer userType;
    private String remark;
    private Integer publicId;
    private String publicName;
    private String appid;
    private String appsecret;
    private String SLD;
    private String creatUser;
    private Long createTime;
    private Long lastUpdateTime;

}
