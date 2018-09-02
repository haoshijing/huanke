package com.huanke.iot.manage.vo.response.format;

import lombok.Data;

import java.util.List;

@Data
public class ModelFormatVo {

    List<DeviceModelFormatPageVo> modelFormatPages;

    @Data
    public static class DeviceModelFormatPageVo {
        private Integer id;
        private Integer modelId;
        private Integer formatId;
        private Integer pageId;
        private Integer showStatus;
        private String showName;
        private Integer status;
        List<DeviceModelFormatItemVo> modelFormatItems;

    }

    @Data
    public static class DeviceModelFormatItemVo {
        private Integer id;
        private Integer modelFormatId;
        private Integer itemId;
        private Integer ablityId;
        private Integer showStatus;
        private String showName;
        private Integer status;
    }
}
