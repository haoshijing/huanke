package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-12 下午4:47
 */
@Data
public class ShareRequest {
    private String masterOpenId;
    private Integer deviceId;
    private String token;
    private Integer teamId;
}
