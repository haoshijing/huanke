package com.huanke.iot.manage.vo.request.device.typeModel;


import lombok.Data;

/**
 * @author caikun
 * @version 2018年08月16日 23:51
 **/
@Data
public class DeviceModelCreateOrUpdateRequest {

    private Integer id;
    private String name;
    private Integer typeId; //类型id
    private Integer customerId;
    private Integer productId;
    private String icon;
    private String version;
    private Integer status;
    private String remark;
    
}
