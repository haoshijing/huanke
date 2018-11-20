package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 设备参数配置req
 *
 * @author onlymark
 * @create 2018-10-30 下午1:11
 */
@Data
public class DeviceParamConfigRequest {
    private Integer deviceId;
    private String abilityDirValue;
    private List<ParamConfig> paramConfigList;

    @Data
    public static class ParamConfig{
        private Integer sort;
        private List<String> valuesList;
    }
}
