package com.huanke.iot.manage.vo.request.device.ability;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAbilityQueryRequest {

    private Integer id;
    private String abilityName;
    private String abilityCode;
    private String dirValue;
    private Integer writeStatus; //可写状态
    private Integer readStatus; //可读状态
    private Integer runStatus; //可执行状态
    private Integer configType;//配置方式
    private Integer abilityType;//能力类型
    private Integer status=CommonConstant.STATUS_YES;

    private Integer page = 1;
    private Integer limit = 20;

    @Data
    public static class DeviceAbilitysRequest {
        private Integer deviceId;
        private List<Integer> abilityIds;
    }

}
