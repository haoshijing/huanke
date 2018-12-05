package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 规则
 *
 * @author onlymark
 * @create 2018-11-14 上午9:52
 */
@Data
public class ProjectRule {
    private Integer id;
    private Integer customerId;
    private Integer useType;//1-计划，2-客户报修反馈，3-设备告警
    private String name;
    private String description;
    private Integer typeId;
    private Integer warnLevel;
    private Integer status;//1-正常；2-删除；3-禁用
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}