package com.huanke.iot.base.po.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月13日 16:10
 **/
@Data
public class CustomerPo {
    private Integer id;
    private Integer parentCustomerId;
    private String name;
    private String loginName;
    private String serviceUser;
    private Integer userType;
    private String remark;
    private String publicId;
    private String publicName;
    private String appid;
    private String appsecret;
    private String SLD;
    private String typeIds;
    private String modelIds;
    private String creatUser;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer status = CommonConstant.STATUS_YES;

}
