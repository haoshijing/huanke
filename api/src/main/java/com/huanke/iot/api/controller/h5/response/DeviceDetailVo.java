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

    private String deviceName;
    private String deviceTypeName;
    private String deviceId;
    private PmDataItem pm;
    private SysDataItem co2;
    private SysDataItem tem;
    private SysDataItem hum;
    private SysDataItem tvoc;
    private SysDataItem hcho;
    private SysDataItem remain;
    private SysDataItem screen;
    private DataItem modeItem;
    private List<OtherItem> windItems;
    private List<JSONArray> funcs;
    private List<OtherItem> timers;

    private String mac;
    private String weather;
    private String area;
    private String date;
    private String aqi;

    private DataItem childItem;

    @Data
    public static class DataItem{
        private String value;
        private String type;
        private String choice;
    }

    @Data
    public static class OtherItem extends  DataItem{
        private String name;
    }

    @Data
    public static class PmDataItem extends  SysDataItem{
        private String  mass;
    }
    @Data
    public static class SysDataItem{
        private String data;
        private String unit;
    }
}
