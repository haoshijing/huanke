package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

/**
 * @author sixiaojun
 * @date 2018/10/31 19:18
 */
@Data
public class UpdateShareRequest {
    private Integer deviceId;
    private String openId;
    private Integer status;
}
