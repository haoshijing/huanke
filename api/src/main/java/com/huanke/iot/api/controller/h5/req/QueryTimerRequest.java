package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-14 下午10:27
 */
@Data
public class QueryTimerRequest {
    private Integer deviceId;
    private Integer type;
}
