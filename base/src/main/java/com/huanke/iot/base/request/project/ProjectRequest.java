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
    private String projectNo;
    private String name;
    private String description;
    private Date buildTime;
    private String buildAddress;
    private String gps;
    private String groupIds;
    private Integer status;
    private List<ExtraDevice> extraDeviceList;
    private List<MaterialInfo> materialInfoList;

    @Data
    public static class ExtraDevice{
        private Integer id;
        private String name;
        private String model;
        private String factory;
        private String direction;
    }

    @Data
    public static class MaterialInfo{
        private Integer id;
        private Integer type;
        private String name;
        private Integer nums;
    }


}
