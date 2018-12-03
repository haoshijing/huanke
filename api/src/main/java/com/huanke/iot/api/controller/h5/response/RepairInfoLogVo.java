package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

import java.util.Date;

@Data
public class RepairInfoLogVo {
    private String name;
    private String description;
    private String status;
    private Date createTime;
}
