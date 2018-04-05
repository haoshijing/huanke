package com.huanke.iot.manage.controller.response;

import lombok.Data;

import java.util.List;

@Data
public class UserDataResponse {
    private List<String> roles;
    private String avatar;
    private String name;
    private String introduction;
}