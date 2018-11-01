package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

import java.util.List;

@Data
public class DeviceModelVo {
    private Integer formatId;
    private String formatShowName;
    private String pageName;
    private Integer modelId;
    private String typeNo;
    private List<Abilitys> abilitysList;
    private List<FormatItems> formatItemsList;

    @Data
    public static class Abilitys{
        private Integer abilityId;
        private String abilityName;
        private String definedName;
        private String dirValue;
        private Integer writeStatus;
        private Integer readStatus;
        private Integer runStatus;
        private String deviceValue;
        private Integer abilityType;
        private Integer minVal;
        private Integer maxVal;
        private String remark;
        private List<AbilityOption> abilityOptionList;
    }

    @Data
    public static class AbilityOption{
        private String optionName;
        private String optionDefinedName;
        private String optionValue;
        private Integer defaultValue;
        private Integer minVal;
        private Integer maxVal;
        private Integer status;
    }

    @Data
    public static class FormatItems{
        private Integer itemId;
        private String showName;
        private Integer showStatus;
        private Integer abilityId;
    }
}
