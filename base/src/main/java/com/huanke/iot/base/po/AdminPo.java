package com.huanke.iot.base.po;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class AdminPo {
    private Integer id;
    private String userName;
    private String password;
    private Long insertTime;
    private Long lastUpdateTime;
    private Integer status = CommonConstant.STATUS_YES;
}
