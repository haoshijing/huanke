package com.huanke.iot.base.po.security.user;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月04日 17:18
 **/
@Data
public class UserPo {
    private Integer id;
    private String userName;
    private String password;
    private Long createTime;
    private Long lastUpdateTime;
}
