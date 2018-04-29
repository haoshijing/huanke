package com.huanke.iot.manage.message;

import lombok.Data;

import java.util.UUID;

@Data
public class OtaDeviceVo {

    private OtaDeviceItem fota;
    private String requestId = UUID.randomUUID().toString().replace("-","");
    @Data
    public static class OtaDeviceItem {
        private String type;
        private VersionPo version;
        private String url;
        private Integer size;
        private String md5;
    }

    @Data
    public static class VersionPo{
        private String software;
        private String hardware;
    }
}
