package com.huanke.iot.api.controller.h5.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月10日 10:21
 **/
@Data
public class DeviceDetailVo {

    private List<JSONArray> deviceSenorData;

    /**
     * 功能列表集合
     */
    private List<JSONArray> deviceFuncData;

    @Data
    public static class DeviceSensorVo{
        private String sensorType;
        private String sensorValue;
        private String senorName;
        private String unit;
    }

    @Data
    public static class DeviceFuncVo{
        /**
         * 功能id
         */
        private String funcId;
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

    }

}
