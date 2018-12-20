package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 * 组内设备关联req
 *
 * @author onlymark
 * @create 2018-12-20 下午3:24
 */
@Data
public class TeamDeviceLinkRequest {
    /**
     * 组id
     */
    private Integer teamId;
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 联动状态
     */
    private Integer linkAgeStatus;
}
