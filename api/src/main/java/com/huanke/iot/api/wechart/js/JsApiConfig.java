package com.huanke.iot.api.wechart.js;

import lombok.Data;

@Data
public class JsApiConfig {
    private boolean debug;
    private String appId;
    private String timestamp;
    private String nonce;
    private String signature;
    private String title;
    private String link;
    private String signType;
    private String packageName;

}
