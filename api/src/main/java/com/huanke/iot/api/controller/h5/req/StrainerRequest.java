package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 滤网request
 *
 * @author onlymark
 * @create 2018-10-17 下午6:58
 */
@Data
public class StrainerRequest {
    private Integer deviceId;
    private List<String> dirValueList;
}
