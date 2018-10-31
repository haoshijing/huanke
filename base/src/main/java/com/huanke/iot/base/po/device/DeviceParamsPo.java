package com.huanke.iot.base.po.device;

import lombok.Data;

/**
 * 描述:
 * 设备传参类
 *
 * @author onlymark
 * @create 2018-10-29 下午1:49
 */
@Data
public class DeviceParamsPo {
    private Integer id;
    private Integer deviceId;
    private Integer abilityId;
    private String typeName;
    private String paramDefineName;
    private String value;
    private String configValues;
    private Integer sort;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer createUserId;
    private Integer updateUserId;
    private Integer updateWay;
}
