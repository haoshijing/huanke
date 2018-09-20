package com.huanke.iot.manage.vo.request.device.typeModel;


import com.huanke.iot.manage.vo.request.device.ability.DeviceTypeAbilitysCreateRequest;
import lombok.Data;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月16日 23:51
 **/
@Data
public class DeviceTypeCreateOrUpdateRequest {


    private Integer id;
    private String name;
    private String typeNo;
    private String stopWatch;
    private String icon;
    private String source;
    private String remark;

    private List<DeviceTypeAbilitysCreateRequest> deviceTypeAbilitys;

}
