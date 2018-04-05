package com.huanke.iot.manage.controller.request;

import lombok.Data;

@Data
public class UpdatePwdRequest {
    private String oldPwd;
    private String newPwd;
}
