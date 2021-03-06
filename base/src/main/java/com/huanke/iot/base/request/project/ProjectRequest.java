package com.huanke.iot.base.request.project;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-14 下午1:48
 */
@Data
public class ProjectRequest {
    private Integer id;
    private String projectNo;  //工程No
    private String name;
    private String description;
    private Date buildTime;     //工程建设实践
    private String buildAddress;    //工程建设地址（各字段用"，"拼接）
    private String gps;     //工程建设经纬度（各字段用"，"拼接）
    private String groupIds;    //关联设备项目ids
    private Integer status;
    private List<ExtraDevice> extraDeviceList;      //第三方设备list
    private List<MaterialInfo> materialInfoList;    //实施信息list

    private String[] imgs;
    @Data
    public static class ExtraDevice{
        private Integer id;
        private String name;
        private String model;
        private String factory;
        private String direction;
        private Date createTime;
    }

    @Data
    public static class MaterialInfo{
        private Integer id;
        private Integer type;
        private String name;
        private String unit;
        private Integer nums;
        private Date createTime;
    }


}
