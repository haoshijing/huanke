package com.huanke.iot.api.controller.h5.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月28日
 **/
@Data
public class DeviceFormatVo {
    private List<formatItem> formatItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class formatItem{
        private Integer formatId;
        private String name;
    }
}
