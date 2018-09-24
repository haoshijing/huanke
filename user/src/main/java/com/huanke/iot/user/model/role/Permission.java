package com.huanke.iot.user.model.role;

import lombok.Data;

@Data
public class Permission {

    private Integer id;

    private String authKey;

    private String action;

    private String authName;

    private Integer parent;
}
