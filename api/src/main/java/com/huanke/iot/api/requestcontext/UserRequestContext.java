package com.huanke.iot.api.requestcontext;


import lombok.Data;

@Data
public class UserRequestContext {

    private Integer currentId;
    private CustomerVo customerVo;

    @Data
    public static class CustomerVo{
        private Integer customerId;
        private String appId;
    }
}
