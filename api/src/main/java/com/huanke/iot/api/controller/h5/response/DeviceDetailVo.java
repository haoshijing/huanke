package com.huanke.iot.api.controller.h5.response;

import lombok.Data;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月10日 10:21
 **/
@Data
public class DeviceDetailVo {
    private Integer childlock;
    private Integer pm;
    private Integer temperature;
    private Integer humidity;
    private Integer co2;
    /**
     * 甲醛值
     */
    private Integer tvoc;
    /**
     * 甲醛化学因子数
     */
    private Integer hcho;

    /**
     * 功能列表集合
     */
    private List<DeviceFuncVo> deviceFuncVoList;

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

        private Integer type;
    }
}
