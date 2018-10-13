package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 设备功能vo
 *
 * @author onlymark
 * @create 2018-09-10 上午9:45
 */
@Data
public class DeviceAbilitysVo {
    private Integer id;
    private String dirValue;
    private String abilityName;
    private Integer abilityType;
    private String currValue;
    private String unit;
    private List<abilityOption> abilityOptionList;

    @Data
    public static class abilityOption{
        private String dirValue;
        private Integer isSelect;
        private String currValue;
    }
}
