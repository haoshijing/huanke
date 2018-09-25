package com.huanke.iot.manage.vo.response.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

/**
 * @author caikun
 * @date 2018/8/19 下午6:10
 **/
@Data
public class DeviceModelAbilityVo {
    private Integer id;
    private Integer modelId;
    private Integer abilityId;
    private String definedName;
    private Integer minVal;
    private Integer maxVal;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;

    private List<DeviceModelAbilityOptionVo> deviceModelAbilityOptions;

    @Data
    public static class DeviceModelAbilityOptionVo {

        private Integer id;
        private Integer abilityOptionId;
        private String definedName;
        private Integer minVal;
        private Integer maxVal;
        private Integer status = CommonConstant.STATUS_YES;

    }
}