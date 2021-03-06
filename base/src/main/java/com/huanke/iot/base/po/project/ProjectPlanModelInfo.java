package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 计划模版表
 *
 * @author onlymark
 * @create 2018-11-14 上午9:48
 */
@Data
public class ProjectPlanModelInfo {
    private Integer id;
    private Integer customerId;
    private String name;
    private String description;
    private Integer isRule;
    private Integer ruleId;
    private Integer linkType;
    private Integer linkDeviceId;
    private Integer linkProjectId;
    private Date warnDate;
    private Date nextExecuteTime;
    private Integer month;
    private Integer day;
    private Integer cycleType;
    private Integer cycleNums;
    private Integer overTimeDays;
    private String enableUsers;
    private Integer status = 1;
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;


    private Integer warnLevel;
}