package com.huanke.iot.manage.vo.response.device.customer;

import lombok.Data;

@Data
public class UserFeedbackInfoVo {
    private Integer id;
    private String userName;
    private String location;
    private String feedbackInfo;
    private Long createTime;
}
