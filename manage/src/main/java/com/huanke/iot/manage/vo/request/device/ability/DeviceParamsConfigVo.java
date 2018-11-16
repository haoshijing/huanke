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
    private String paramName;
    private String abilityTypeName;
    private Integer sort;
    private List<ConfigValue> configValuesList;

    @Data
    public static class ConfigValue {
        private String definedName;
        private Integer defaultValue;
        private Integer minValue;
        private Integer maxValue;
        private Integer currentValue;
    }
}
