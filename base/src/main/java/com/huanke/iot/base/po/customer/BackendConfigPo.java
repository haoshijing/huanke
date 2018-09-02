package com.huanke.iot.base.po.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * 后端配置表
 */
@Data
public class BackendConfigPo {

    private Integer id;
    private String name;    //管理后台名称
    private String logo;    //管理后台的logo
    private Integer type;   //类型
    private Integer enableStatus;   //后台是否可用
    private Integer customerId;
    private Integer status = CommonConstant.STATUS_YES;   //
    private Long createTime;
    private Long lastUpdateTime;
}
