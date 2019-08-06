package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

import java.util.List;

@Data
public class MaBiaoConfigReq {

    private List<String> deviceIds;
    private String path;
}
