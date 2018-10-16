package com.huanke.iot.manage.vo.request.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

/**
 * 保存客户信息
 */
@Data
public class CustomerQueryRequest {

    private Integer id;
    private Integer parentCustomerId;
    private String name;
    private String publicName;
    private String publicId;
    private String appid;
    private String appsecret;
    private Integer userType;
    private String loginName;
    private String SLD;
    private Integer status = CommonConstant.STATUS_YES;

    private Integer page = 1;
    private Integer limit = 20;

}
