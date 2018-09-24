package com.huanke.iot.user.model.role;

import lombok.Data;

import java.util.List;

@Data
public class Role2PermissionRsp {

    private Role role;

    private List<Permission> permissions;
}
