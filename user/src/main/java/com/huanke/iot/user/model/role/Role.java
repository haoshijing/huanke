package com.huanke.iot.user.model.role;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Role {

    private Integer id;

    private String roleName;

    private String roleDesc;

    private Integer userCount;

    private Integer creater;

    private Date createTime;

    private Date lastUpdateTime;
}
