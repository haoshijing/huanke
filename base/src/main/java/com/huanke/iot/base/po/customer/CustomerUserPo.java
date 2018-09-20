package com.huanke.iot.base.po.customer;

import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
@Data
public class CustomerUserPo {
    private Integer id;
    private Integer customerId;
    private String openId;
    private String nickname;
    private String unionid;
    private String headimgurl;
    private Integer sex;
    private String province;
    private String city;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
    private Long lastVisitTime;
    private String mac;

}
