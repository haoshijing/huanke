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
        private Integer sensorValue;
        private String senorName;
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

    public static void main(String[] args) {
        DeviceDetailVo detailVo = new DeviceDetailVo();
        DeviceSensorVo deviceSensorVo = new DeviceSensorVo();
        deviceSensorVo.setSenorName("室外PM2.5");
        deviceSensorVo.setSensorType("110");
        deviceSensorVo.setSensorValue(3);

        DeviceSensorVo deviceSensorVo1 = new DeviceSensorVo();
        deviceSensorVo.setSenorName("室外PM2.5");
        deviceSensorVo.setSensorType("111");
        deviceSensorVo.setSensorValue(4);

        DeviceSensorVo  deviceCols = new DeviceSensorVo();
        deviceSensorVo.setSenorName("室内二氧化碳");
        deviceSensorVo.setSensorType("120");
        deviceSensorVo.setSensorValue(30);

        List<JSONArray> list = Lists.newArrayList();
        JSONArray jsonArray = new JSONArray();

        jsonArray.add(deviceSensorVo);
        jsonArray.add(deviceSensorVo1);

        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.add(deviceCols);

        list.add(jsonArray1);
        list.add(jsonArray);
        detailVo.setDeviceSenorData(list);

        DeviceFuncVo deviceFuncVo = new DeviceFuncVo();
        deviceFuncVo.setFuncType(2);
        deviceFuncVo.setFuncId("200");
        deviceFuncVo.setCurrentValue("2");
        deviceFuncVo.setRange("0,1");

        JSONArray jsonArray2 = new JSONArray();
        jsonArray2.add(deviceFuncVo);

        List<JSONArray> jsonArrays = Lists.newArrayList();
        jsonArrays.add(jsonArray2);

        detailVo.setDeviceFuncData(jsonArrays);

        System.out.println( JSON.toJSONString(detailVo) );

    }
}
