package com.huanke.iot.base.enums;

import lombok.Getter;
import lombok.Setter;

public enum AbilityEnums {
    WindStalls(1, "C10");//风速档位

    AbilityEnums(Integer abilityNo, String typeName) {
        this.abilityNo = abilityNo;
        this.typeName = typeName;
    }

    @Getter
    @Setter
    private Integer abilityNo;
    @Getter
    @Setter
    private String typeName;

    public static String getTypeNameByAbilityNo(Integer abilityNo) {
        for (AbilityEnums abilityEnums : AbilityEnums.values()) {
            if (abilityEnums.getAbilityNo() == abilityNo) {
                return abilityEnums.getTypeName();
            }
        }
        return null;
    }
}
