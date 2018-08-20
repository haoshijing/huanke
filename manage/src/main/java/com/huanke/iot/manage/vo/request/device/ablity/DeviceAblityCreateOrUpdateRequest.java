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
    private Integer writeStatus; //可读写状态
    private Integer writeType;//配置方式

    private List<DeviceAblityOptionCreateOrUpdateRequest> deviceAblityOptionCreateOrUpdateRequests;
}
