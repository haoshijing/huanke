package com.huanke.iot.api.controller.h5.response;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月10日 10:21
 **/
@Data
public class DeviceDetailVo {

    private List<List<DeviceSensorVo>> deviceSenorData;

    /**
     * 功能列表集合
     */
    private List<List<DeviceFuncVo>> deviceFuncData;

    @Data
    public static class DeviceSensorVo{
        private Integer sensorType;
        private String sensorValue;
        private String senorName;
    }

    @Data
    public static class DeviceFuncVo{
        /**
         * 功能id
         */
        private Integer funcId;
        /**
         * 功能名称
         */
        private String funcName;
        /**
         * 当前值
         */
        private String currentValue;

        /**
         * 功能类型2-开关 3-定时
         */
        private Integer funcType;
        /**
         * 范围 0,1
         */
        private String range;

        private String funcTypeName;
    }
}
