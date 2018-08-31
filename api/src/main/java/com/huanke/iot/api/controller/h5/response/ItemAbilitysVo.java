package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

import java.util.List;

@Data
public class ItemAbilitysVo {
    private Integer formatId;
    private String itemName;
    private List<Abilitys> abilitysList;

    @Data
    public static class Abilitys{
        private String ablityName;
        private String definedName;
        private String dirValue;
        private Integer writeStatus;
        private Integer readStatus;
        private Integer runStatus;
        private String deviceValue;
        private Integer ablityType;
        private List<AbilityOption> abilityOptionList;
    }

    @Data
    public static class AbilityOption{
        private String optionName;
        private String optionDefinedName;
        private String optionValue;
    }
}
