package com.huanke.iot.base.po.user;

import lombok.Data;

import java.util.List;

@Data
public class LoginRsp {

    private User user;

    private List<String> permissions;
}
