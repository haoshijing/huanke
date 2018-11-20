package com.huanke.iot.base.po.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

@Data
public class UserFeedbackPo {
    private Integer id;
    private Integer userId;
    private Integer deviceId;
    private String feedbackInfo;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;


    private String userName;
    private String location;
}
