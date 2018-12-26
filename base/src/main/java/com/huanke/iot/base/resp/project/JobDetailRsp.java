package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 * 任务返回详情
 *
 * @author onlymark
 * @create 2018-11-19 下午1:14
 */
@Data
public class JobDetailRsp {
    private Integer id;
    private Integer type;//关联类型：0-不关联；1-关联设备；2-关联工程
    private Integer linkDeviceId;//关联设备id
    private Integer linkProjectId;//关联工程id
    private String linkProjectName;//关联工程名称
    private String name;
    private String description;
    private Integer isRule;
    private Integer ruleId;
    private String ruleName;
    private String ruleDescription;
    private Integer warnLevel;
    private Integer sourceType;//来源类型：1-计划维保；2-H5端反馈；3-设备告警
    private Date finalTime;
    private Integer warnStatus;//1-正常；2-告警
    private Integer flowStatus;//1-待处理；2-处理中；3-待审核；4-已完成；5-已忽略
    private List<LinkDevice> deviceList;
    private List<HistoryData> historyDataList;


    @Data
    public static class LinkDevice{
        private String mac;
        private String name;
    }

    @Data
    public static class HistoryData{
        private Date createTime;
        private String userName;
        private Integer userId;
        private Integer operateType;
        private String description;
        private List<String> imgList;
        private List<JobMateria> jobMateriaList;
    }
}
