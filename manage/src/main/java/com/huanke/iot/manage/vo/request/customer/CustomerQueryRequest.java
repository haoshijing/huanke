package com.huanke.iot.manage.vo.request.customer;

import lombok.Data;

import java.util.List;

/**
 * 保存客户信息
 */
@Data
public class CustomerQueryRequest {

    private Integer id;
    private String name;
    private String publicName;
    private Integer publicId;
    private String appid;
    private String appsecret;
    private Integer userType;
    private String loginName;
    private String SLD;
    private Integer status;

    private Integer page = 1;
    private Integer limit = 20;

}
