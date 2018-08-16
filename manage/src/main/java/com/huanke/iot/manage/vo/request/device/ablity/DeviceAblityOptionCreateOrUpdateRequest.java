package com.huanke.iot.manage.vo.request.device.ablity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityOptionCreateOrUpdateRequest {

    private Integer id;
    private String optionName;
    private String ablityId;
}
