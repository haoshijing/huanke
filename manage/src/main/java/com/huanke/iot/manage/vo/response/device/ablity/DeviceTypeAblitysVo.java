package com.huanke.iot.manage.vo.response.device.ablity;

import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceTypeAblitysVo {

    private Integer id;
    private Integer ablityId;
    private Integer ablityType;
    private String ablityName;
    private Integer typeId;

    private List<DeviceAblityOptionVo> deviceAblityOptions;
}
