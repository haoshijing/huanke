package com.huanke.iot.manage.vo.request.customer;

import lombok.Data;

@Data
public class UserFeedbackInfoVoReq {
    private Integer userId;
    private Integer deviceId;
    private String userName;
    private String location;
    private Integer status=1;
    private Integer beginTime;
    private Integer endTime;
    private Integer limit=20;
    private Integer offset=0;
    private Integer customerId;
}
