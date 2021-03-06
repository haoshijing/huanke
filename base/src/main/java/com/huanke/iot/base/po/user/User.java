package com.huanke.iot.base.po.user;

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

    private String location;

    private Integer roleId;

    private String roleName;

    private String openID;

    private String appId;

    private String appSecret;

    private String secondDomain;

    private String customerName;

    //启用enable,禁用:disable,限制：limited
    private String status;

    private Integer templateId;

    private Integer createUser;

    private String createUserName;

    private Date createTime;

    private Date lastUpdateTime;

    private Integer lastUpdateUser;

    private String lastUpdateUserName;

}
