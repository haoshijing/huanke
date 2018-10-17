package com.huanke.iot.base.po.role;

import lombok.Data;

import java.util.List;

@Data
public class Role2PermissionReq {

    private Role role;

    private List<Integer> permissions;
}
