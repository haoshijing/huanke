package com.huanke.iot.user.model.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String userName;

    private String password;

    private String realName;

    private String nickName;

    private String telephone;

    private Integer roleId;

    private String roleName;

    private String openID;

    private String appId;

    private String appSecret;

    private String secondDomain;

    //启用enable,禁用:disable,限制：limited
    private String status;

    private Integer templateId;

    private Date createTime;

    private Date lastUpdateTime;

}
