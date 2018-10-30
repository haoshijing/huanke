package com.huanke.iot.manage.vo.request.device.ability;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 设备传参request
 *
 * @author onlymark
 * @create 2018-10-29 下午6:18
 */
@Data
public class DeviceParamsConfigVoRequest {
    private Integer deviceId;
    private List<DeviceParamsConfigVo> deviceParamsConfigVoList;
}
