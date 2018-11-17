package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

@Data
public class UserFeedbackRequest {
    private Integer deviceId;
    private String feedbackInfo;
}
