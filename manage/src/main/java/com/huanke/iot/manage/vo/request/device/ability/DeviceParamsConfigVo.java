package com.huanke.iot.manage.vo.request.device.ability;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 设备传参详情Vo
 *
 * @author onlymark
 * @create 2018-10-29 下午3:15
 */
@Data
public class DeviceParamsConfigVo {
    private Integer abilityId;
    private String modelAbilityName;
    private String abilityParamsName;
    private Integer sort;
    private List<String> valuesList;
}
