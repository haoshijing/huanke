package com.huanke.iot.api.requestcontext;


import lombok.Data;

@Data
public class UserRequestContext {

    private Integer currentId;
    private String openId;
    private CustomerVo customerVo;
    private String appNo;

    private String requestInfo;
    @Data
    public static class CustomerVo{
        private Integer customerId;
        private String appId;
    }
}
