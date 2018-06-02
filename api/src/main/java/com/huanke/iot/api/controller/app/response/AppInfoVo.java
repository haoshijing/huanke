package com.huanke.iot.api.controller.app.response;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年04月27日 12:06
 **/
@Data
public class AppInfoVo {
    private String versionCode = "";
    private String apkUrl;
    private String versionName = "";
}
