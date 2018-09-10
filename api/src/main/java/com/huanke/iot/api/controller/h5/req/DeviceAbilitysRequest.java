package com.huanke.iot.api.controller.h5.req;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述:
 * 设备功能req
 *
 * @author onlymark
 * @create 2018-09-10 上午9:20
 */
@Data
@NoArgsConstructor
public class DeviceAbilitysRequest {
    private String deviceId;
    private List<Integer> abilityIds;
}
