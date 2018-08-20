package com.huanke.iot.manage.vo.request.device.ablity;

import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityOptionVo;
import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityCreateOrUpdateRequest {

    private Integer id;
    private String ablityName;
    private String dirValue;
    private String remark;
    private Integer writeStatus; //可写状态
    private Integer readStatus; //可读状态
    private Integer runStatus; //可执行状态
    private Integer configType;//配置方式

    private List<DeviceAblityOptionCreateOrUpdateRequest> deviceAblityOptionCreateOrUpdateRequests;
}
