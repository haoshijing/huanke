package com.huanke.iot.base.po.role;

import lombok.Data;

import java.util.List;

@Data
public class Role2PermissionRsp {

    private Role role;

    private List<Permission> permissions;
}
