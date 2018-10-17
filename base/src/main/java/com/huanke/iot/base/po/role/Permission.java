package com.huanke.iot.base.po.role;

import lombok.Data;

@Data
public class Permission {

    private Integer id;

    private String authKey;

    private String action;

    private String authName;

    private Integer parent;
}
