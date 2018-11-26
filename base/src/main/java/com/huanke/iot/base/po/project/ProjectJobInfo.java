package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 任务表
 *
 * @author onlymark
 * @create 2018-11-14 上午9:26
 */
@Data
public class ProjectJobInfo {
    private Integer id;
    private Integer customerId;
    private Integer type;//关联类型：1-设备；2-工程
    private Integer linkDeviceId;//关联设备id
    private Integer linkProjectId;//关联工程id
    private String name;
    private String description;
    private Integer isRule;
    private Integer ruleId;
    private Integer warnLevel;
    private Integer sourceType;//来源类型：1-计划维保；2-H5端反馈；3-设备告警
    private Integer planId;
    private Date finalTime;
    private String imgList;//图册
    private String enableUsers;
    private Integer warnStatus;//1-正常；2-告警
    private Integer flowStatus;//1-待处理；2-处理中；3-待审核；4-已完成；5-已忽略
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}