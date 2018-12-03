package com.huanke.iot.api.controller.h5.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class RuleInfoVo {
    private String dictName;
    private Integer dictId;
    private List<RuleInfo> rules;

    @Data
    @NoArgsConstructor
    public static class RuleInfo{
        private Integer id;
        private String name;
        private String description;
    }
}
