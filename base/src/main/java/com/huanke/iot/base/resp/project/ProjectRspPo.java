package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-16 上午9:42
 */
@Data
public class ProjectRspPo {
    private Integer id;
    private String projectNo;
    private String name;
    private String description;
    private Date buildTime;
    private String buildAddress;
    private String groupIds;
    private String[] imgList;
    private Integer projectCount;
    private Integer deviceCount;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private String createName;
    private String updateName;
    private String imgs;

    @Data
    public static class ProjectPercent{
        private String buildAddress;
        private String distance;
        private Long projectCount;
        private String projectPercent;
    }
    @Data
    public static class ProjectCountVo {
        private String date;
        private Long projectCount;
    }
}
