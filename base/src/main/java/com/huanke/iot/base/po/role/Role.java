package com.huanke.iot.base.po.role;

import lombok.Data;

import java.util.Date;

@Data
public class Role {

    private Integer id;

    private String roleName;

    private String roleDesc;

    private Integer userCount;

    private String secondDomain;

    private Integer creater;

    private Date createTime;

    private Date lastUpdateTime;

    private Integer createUserId;
    private Integer updateUserId;
}
