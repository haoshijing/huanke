package com.huanke.iot.api.controller.h5.req;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 上午11:29
 */
@Data
@NoArgsConstructor
public class ChildDeviceRequest {
    private Integer hostDeviceId;
    private String childId;
    private String deviceName;
    private Integer modelId;
}
