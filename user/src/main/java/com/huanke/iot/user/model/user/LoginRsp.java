package com.huanke.iot.user.model.user;

import com.huanke.iot.user.model.role.Permission;
import lombok.Data;

import java.util.List;

@Data
public class LoginRsp {

    private User user;

    private List<String> permissions;
}
