package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 * 组开关req
 *
 * @author onlymark
 * @create 2018-12-13 上午8:13
 */
@Data
public class OccRequest {
    private String funcId;
    private String value;
    private Integer teamId;
    private Integer openCloseStatus;//开关：0-关，1-开
}
