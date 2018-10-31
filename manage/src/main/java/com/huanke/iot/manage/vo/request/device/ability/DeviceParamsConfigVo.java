package com.huanke.iot.manage.vo.request.device.ability;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
    private String paramDefineName;
    private String typeName;
    private Integer sort;
    private List<String> valuesList;
    private List<Map<String, MinMaxConfig>> defaultConfig;

    @Data
    private static class MinMaxConfig {
        private String minValue;
        private String maxValue;
    }
}
