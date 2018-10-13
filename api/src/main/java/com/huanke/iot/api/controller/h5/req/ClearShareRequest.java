package com.huanke.iot.api.controller.h5.req;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述:
 * 删除用户设备关系数据request
 *
 * @author onlymark
 * @create 2018-10-13 下午2:13
 */
@Data
@NoArgsConstructor
public class ClearShareRequest {
    private Integer deviceId;
    private String openId;
}
