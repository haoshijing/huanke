package com.huanke.iot.manage.vo.request.device.typeModel;


import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月16日 23:51
 **/
@Data
public class DeviceTypeCreateOrUpdateRequest {


    private Integer id;
    private String name;
    private String typeNo;
    private String icon;
    private String remark;


}
