package com.huanke.iot.manage.vo.response.device.ability;

import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAbilityVo {

    private Integer id;
    private String abilityName;
    private String dirValue;
    private String remark;
    private Integer writeStatus; //可写状态
    private Integer readStatus; //可读状态
    private Integer runStatus; //可执行状态
    private Integer configType;//配置方式
    private Integer abilityType;//能力类型
    private Integer minVal;
    private Integer maxVal;

    private List<DeviceAbilityOptionVo> deviceAbilityOptions;


    @Data
    public static class DeviceAbilitysVo {
        private Integer id;
        private String dirValue;
        private String abilityName;
        private Integer abilityType;
        private String currValue;
        private String unit;
        private List<abilityOption> abilityOptionList;

    }

    @Data
    public static class abilityOption{
        private String dirValue;
        private Integer isSelect;
        private String currValue;
    }

}
