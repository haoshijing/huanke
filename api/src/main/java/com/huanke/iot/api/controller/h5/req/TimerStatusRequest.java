package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-14 下午11:50
 */
@Data
public class TimerStatusRequest {
    private Integer timerId;
    private Integer status;
}
