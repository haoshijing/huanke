package com.huanke.iot.manage.vo.request.type;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年06月01日 12:58
 **/
@Data
public class DeviceTypeResponseVo {
    private Integer id;
    private String typeName;
    private String funcListStr;
    private String funcList;
    private String sensorListStr;
    private String sensorList;
    private String icon;
}
