package com.huanke.iot.base.po.customer;

import lombok.Data;

@Data
public class UserFeedbackPo {
    private Integer Id;
    private Integer userId;
    private Integer deviceId;
    private String feedbackInfo;
    private Long createTime;
}
