package com.huanke.iot.base.po.user;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月09日 09:34
 **/
@Data
public class AppUserPo {
    private Integer id;
    private String openId;
    private Integer sex;
    private String province;
    private String city;
    private String nickname;
    private  String unionid;
    private String headimgurl;
    private Long createTime;
    private Long lastUpdateTime;
    private Long lastVisitTime;
    /**
     * 绑定的安卓客户端地址
     */
    private String androidMac;
}
