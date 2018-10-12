package com.huanke.iot.manage.vo.request.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author caikun
 * @date 2018年08月17日 00:01
 **/
@Data
public class DeviceTypeQueryRequest {

    private Integer id;
    private String name;
    private String typeNo;
    private Integer status=CommonConstant.STATUS_YES;
    private Integer page = 1;
    private Integer limit = 20;
}
